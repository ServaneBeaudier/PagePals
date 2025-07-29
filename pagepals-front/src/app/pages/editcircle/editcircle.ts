import { Component, OnInit } from '@angular/core';
import { BookDTO, CircleDTO, CircleService, Genre } from '../../core/circle.service';
import { debounceTime, distinctUntilChanged, Observable, of, switchMap } from 'rxjs';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { TokenStorage } from '../../core/token-storage';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { AddressAutocomplete, NominatimResult } from '../../address-autocomplete/address-autocomplete';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function dateNotInPastValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(control.value);
    return selectedDate < today ? { dateInPast: true } : null;
  };
}

@Component({
  selector: 'app-edit-circle',
  imports: [CommonModule, RouterModule, MatSelectModule, FormsModule, ReactiveFormsModule, AddressAutocomplete],
  templateUrl: './editcircle.html',
  styleUrl: './editcircle.css'
})
export class Editcircle implements OnInit {
  editCircleForm!: FormGroup;
  genres: Genre[] = [];
  filteredBooks: BookDTO[] = [];
  selectedBook?: BookDTO;

  searchingBooks = false;
  bookSearchFailed = false;

  circleId!: number; // id du cercle à modifier
  createurId!: number;

  dropdownOpen = false;
  selectedGenres: number[] = [];

  selectedAddress: NominatimResult | null = null;

  constructor(
    private fb: FormBuilder,
    private circleService: CircleService,
    private tokenStorage: TokenStorage,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.editCircleForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      modeRencontre: [null, Validators.required],
      dateRencontre: ['', [Validators.required, dateNotInPastValidator()]],
      nbMaxMembres: [10, [Validators.required, Validators.min(1), Validators.max(20)]],
      genreIds: [[], [Validators.required, Validators.minLength(1)]],
      lieuRencontre: [''],
      lieuRencontreDetails: this.fb.group({
        shop: [''],
        houseNumber: [''],
        road: [''],
        postcode: [''],
        city: ['']
      }),
      lienVisio: [''],
      livrePropose: ['']
    });

    this.circleService.getGenres().subscribe({
      next: (genres) => {
        this.genres = genres;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des genres', err);
      }
    });

    this.circleId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.circleId) {
      this.loadCircleData(this.circleId);
    } else {
      alert('Aucun identifiant de cercle fourni');
      this.router.navigate(['/profile']);
    }

    this.editCircleForm.get('livrePropose')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => {
        const query = typeof value === 'string' ? value.trim() : '';
        if (!query || query.length < 2) return of([]); // évite les requêtes trop courtes
        return this.searchBooks(query);
      })
    ).subscribe({
      next: books => {
        this.filteredBooks = books;
        this.bookSearchFailed = false;
      },
      error: () => {
        this.filteredBooks = [];
        this.bookSearchFailed = true;
      }
    });

    const updateValidators = (mode: string) => {
      if (mode === 'PRESENTIEL') {
        this.editCircleForm.get('lieuRencontre')?.setValidators([Validators.required]);
        this.editCircleForm.get('lienVisio')?.clearValidators();
        this.editCircleForm.get('lienVisio')?.setValue('');
      } else {
        this.editCircleForm.get('lieuRencontre')?.clearValidators();
        this.editCircleForm.get('lieuRencontre')?.setValue('');
        this.editCircleForm.get('lienVisio')?.clearValidators();
      }
      this.editCircleForm.get('lieuRencontre')?.updateValueAndValidity();
      this.editCircleForm.get('lienVisio')?.updateValueAndValidity();
    };

    // Applique les validateurs au démarrage selon la valeur actuelle de modeRencontre
    updateValidators(this.editCircleForm.get('modeRencontre')?.value);

    // Applique les validateurs à chaque changement de modeRencontre
    this.editCircleForm.get('modeRencontre')?.valueChanges.subscribe(mode => {
      updateValidators(mode);
    });
  }

  loadCircleData(id: number): void {
    this.circleService.getCircleById(id).subscribe({
      next: circle => {
        const descriptionClean = (circle.description === null || circle.description === 'null') ? '' : circle.description;
        const nbMax = Number(circle.nbMaxMembres) || 10;
        const genres = Array.isArray(circle.genreIds) ? circle.genreIds : [];

        // Pour dateRencontre : format compatible datetime-local
        let dateRencontreFormatted = '';
        if (circle.dateRencontre) {
          dateRencontreFormatted = circle.dateRencontre.length > 16 ? circle.dateRencontre.substring(0, 16) : circle.dateRencontre;
        }

        this.editCircleForm.patchValue({
          nom: circle.nom,
          description: descriptionClean,
          modeRencontre: circle.modeRencontre,
          dateRencontre: dateRencontreFormatted,
          nbMaxMembres: nbMax,
          genreIds: genres,
          lieuRencontre: circle.lieuRencontre || '',
          lienVisio: circle.lienVisio || '',
          livrePropose: circle.livrePropose ? this.formatBookForForm(circle.livrePropose) : ''
        });

        this.editCircleForm.get('lieuRencontreDetails')?.patchValue({
          shop: circle.lieuRencontreDetails?.shop || '',
          houseNumber: circle.lieuRencontreDetails?.houseNumber || '',
          road: circle.lieuRencontreDetails?.road || '',
          postcode: circle.lieuRencontreDetails?.postcode || '',
          city: circle.lieuRencontreDetails?.city || ''
        });

        this.selectedGenres = genres;
        this.editCircleForm.get('modeRencontre')?.setValue(circle.modeRencontre, { emitEvent: true });

        const modeControl = this.editCircleForm.get('modeRencontre');
        if (modeControl) {
          modeControl.updateValueAndValidity();
          modeControl.markAsDirty();
          modeControl.markAsTouched();
        }

        // Ajuster les validateurs
        if (circle.modeRencontre === 'PRESENTIEL') {
          this.editCircleForm.get('lieuRencontre')?.setValidators([Validators.required]);
          this.editCircleForm.get('lienVisio')?.clearValidators();
        } else {
          this.editCircleForm.get('lieuRencontre')?.clearValidators();
          this.editCircleForm.get('lienVisio')?.setValidators([Validators.required]);
        }
        this.editCircleForm.get('lieuRencontre')?.updateValueAndValidity();
        this.editCircleForm.get('lienVisio')?.updateValueAndValidity();

        this.selectedBook = circle.livrePropose ?? undefined;

        console.log('Form values after patch:', this.editCircleForm.value);
      },
      error: err => {
        alert('Erreur lors du chargement du cercle');
        this.router.navigate(['/profile']);
      }
    });
  }

  formatBookForForm(book: BookDTO): string {
    return `${book.titre} (${book.auteurs.join(', ')})`;
  }

  searchBooks(query: string): Observable<BookDTO[]> {
    this.searchingBooks = true;
    return this.circleService.searchBooks(query);
  }

  displayBook(book?: BookDTO): string {
    if (!book) return '';
    const titre = book.titre ?? 'Titre inconnu';
    const auteurs = Array.isArray(book.auteurs) && book.auteurs.length
      ? book.auteurs.join(', ')
      : 'Auteur inconnu';
    return `${titre} (${auteurs})`;
  }

  onBookSelected(book: BookDTO): void {
    this.selectedBook = book;
    this.editCircleForm.patchValue({ livrePropose: book });
    this.filteredBooks = [];
  }

  onLivreInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;
    const control = this.editCircleForm.get('livrePropose');
    if (control) {
      control.setValue(value);
    }
  }


  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  onGenreChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const id = +input.value;
    if (input.checked) {
      if (!this.selectedGenres.includes(id)) {
        this.selectedGenres.push(id);
      }
    } else {
      this.selectedGenres = this.selectedGenres.filter(g => g !== id);
    }
    this.editCircleForm.patchValue({ genreIds: this.selectedGenres });
  }

  isGenreSelected(id: number): boolean {
    return this.selectedGenres.includes(id);
  }

  submit(): void {
    if (this.editCircleForm.invalid) {
      this.editCircleForm.markAllAsTouched();
      return;
    }

    const token = this.tokenStorage.getToken();
    if (!token) {
      alert('Vous devez être connecté pour modifier un cercle.');
      return;
    }

    const updateurId = this.authService.getUserIdFromStorage();
    if (!updateurId) {
      alert('Utilisateur non identifié.');
      return;
    }

    const formValue = this.editCircleForm.value;

    const dto: CircleDTO = {
      id: this.circleId,
      nom: formValue.nom,
      description: formValue.description,
      modeRencontre: formValue.modeRencontre,
      dateRencontre: formValue.dateRencontre,
      nbMaxMembres: formValue.nbMaxMembres,
      genreIds: formValue.genreIds,
      lieuRencontre: formValue.lieuRencontre || undefined,
      lieuRencontreDetails: formValue.lieuRencontreDetails || undefined,
      lienVisio: formValue.lienVisio || undefined,
      livrePropose: this.selectedBook,
      createurId: this.createurId,
    };

    console.log('Données envoyées au backend:', dto);

    this.circleService.updateCircle(dto, token).subscribe({
      next: () => {
        this.router.navigate(['/profile'], { state: { message: 'Votre cercle a bien été modifié' } });
      },
      error: err => {
        alert('Erreur lors de la modification du cercle.');
      }
    });

    console.log('Données envoyées au backend:', dto);
  }

  showDeleteModal = false; // contrôle l'affichage du modal

  openDeleteModal() {
    this.showDeleteModal = true;
  }

  cancelDelete() {
    this.showDeleteModal = false;
  }

  deleteCircle(): void {
    // Cette méthode ne sera appelée que quand l'utilisateur confirme dans le modal
    const token = this.tokenStorage.getToken();
    if (!token) {
      alert('Vous devez être connecté pour supprimer un cercle.');
      return;
    }

    this.circleService.deleteCircle(this.circleId, token).subscribe({
      next: () => {
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        alert('Erreur lors de la suppression du cercle.');
        console.error(err);
      }
    });
  }

  onAddressSelected(address: NominatimResult) {
    this.selectedAddress = address;
    this.editCircleForm.patchValue({
      lieuRencontre: address.display_name
    });
    this.editCircleForm.get('lieuRencontreDetails')?.patchValue({
      shop: address.address.shop || '',
      houseNumber: address.address.house_number || '',
      road: address.address.road || '',
      postcode: address.address.postcode || '',
      city: address.address.city || address.address.town || ''
    });
  }


}
