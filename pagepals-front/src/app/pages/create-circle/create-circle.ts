import { Component, OnInit } from '@angular/core';
import { BookDTO, CircleDTO, CircleService, Genre } from '../../core/circle.service';
import { debounceTime, distinctUntilChanged, Observable, of, switchMap } from 'rxjs';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { TokenStorage } from '../../core/token-storage';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { AddressAutocomplete, NominatimResult } from '../../address-autocomplete/address-autocomplete';


@Component({
  selector: 'app-create-circle',
  imports: [CommonModule, RouterModule, ReactiveFormsModule, FormsModule, MatSelectModule, AddressAutocomplete],
  templateUrl: './create-circle.html',
  styleUrl: './create-circle.css'
})
export class CreateCircle implements OnInit {
  createCircleForm!: FormGroup;
  genres: Genre[] = [];
  filteredBooks: BookDTO[] = [];
  selectedBook?: BookDTO;

  searchingBooks = false;
  bookSearchFailed = false;

  constructor(
    private fb: FormBuilder,
    private circleService: CircleService,
    private tokenStorage: TokenStorage,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.createCircleForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      modeRencontre: ['ENLIGNE', Validators.required],
      dateRencontre: ['', Validators.required], // date + heure en iso string
      nbMaxMembres: [10, [Validators.required, Validators.min(1), Validators.max(50)]],
      genreIds: [[]], // tableau d'ids des genres sélectionnés
      lieuRencontre: [''],
      lienVisio: [''],
      livrePropose: [null]
    });

    this.circleService.getGenres().subscribe({
      next: (genres) => {
        this.genres = genres;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des genres', err);
      }
    });

    // Recherche livres en écoutant les changements sur un champ spécifique (à créer dans le HTML)
    this.createCircleForm.get('livrePropose')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => value && typeof value === 'string' ? this.searchBooks(value) : of([]))
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

    // Gestion des champs conditionnels selon modeRencontre
    this.createCircleForm.get('modeRencontre')?.valueChanges.subscribe(mode => {
      if (mode === 'EN_LIGNE') {
        this.createCircleForm.get('lieuRencontre')?.clearValidators();
        this.createCircleForm.get('lieuRencontre')?.setValue('');
      } else {
        this.createCircleForm.get('lieuRencontre')?.setValidators([Validators.required]);
        this.createCircleForm.get('lienVisio')?.clearValidators();
        this.createCircleForm.get('lienVisio')?.setValue('');
      }
      this.createCircleForm.get('lienVisio')?.updateValueAndValidity();
      this.createCircleForm.get('lieuRencontre')?.updateValueAndValidity();
    });
  }

  loadGenres(): void {
    this.circleService.getGenres().subscribe({
      next: genres => this.genres = genres,
      error: err => console.error('Erreur chargement genres', err)
    });
  }

  searchBooks(query: string): Observable<BookDTO[]> {
    this.searchingBooks = true;
    return this.circleService.searchBooks(query).pipe(
    );
  }

  displayBook(book?: BookDTO): string {
    return book ? `${book.titre} (${book.auteurs.join(', ')})` : '';
  }

  onBookSelected(book: BookDTO): void {
    this.selectedBook = book;
    this.createCircleForm.patchValue({ livrePropose: book });
    this.filteredBooks = [];
  }

  submit(): void {
    if (this.createCircleForm.invalid) {
      this.createCircleForm.markAllAsTouched();
      return;
    }

    const token = this.tokenStorage.getToken();
    if (!token) {
      alert('Vous devez être connecté pour créer un cercle.');
      return;
    }

    const createurId = this.authService.getUserIdFromStorage();
    if (!createurId) {
      alert('Utilisateur non identifié.');
      return;
    }

    const formValue = this.createCircleForm.value;

    const dto: CircleDTO = {
      nom: formValue.nom,
      description: formValue.description,
      modeRencontre: formValue.modeRencontre,
      dateRencontre: formValue.dateRencontre,
      nbMaxMembres: formValue.nbMaxMembres,
      genreIds: formValue.genreIds,
      lieuRencontre: formValue.lieuRencontre || undefined,
      lienVisio: formValue.lienVisio || undefined,
      createurId: createurId,
      livrePropose: this.selectedBook
    };

    this.circleService.createCircle(dto, token).subscribe({
      next: () => {
        this.router.navigate(['/profile'], { state: { message: 'Votre cercle a bien été créé' } });
      },
      error: err => {
        alert('Erreur lors de la création du cercle.');
      }
    });
  }

  onLivreInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;
    const control = this.createCircleForm.get('livrePropose');
    if (control) {
      control.setValue(value);
    }
  }

  dropdownOpen = false;
  selectedGenres: number[] = [];

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
    this.createCircleForm.patchValue({ genreIds: this.selectedGenres });
  }

  isGenreSelected(id: number): boolean {
    return this.selectedGenres.includes(id);
  }

  selectedAddress: NominatimResult | null = null;

  onAddressSelected(address: NominatimResult) {
    this.selectedAddress = address;
    this.createCircleForm.patchValue({ lieuRencontre: address.display_name });
  }

}
