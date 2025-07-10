import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CircleDTO, SearchCriteriaDTO, SearchService } from '../../core/search-service';
import { RouterLink } from '@angular/router';
import { CircleService, Genre } from '../../core/circle.service';

@Component({
  selector: 'app-search',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './search.html',
  styleUrl: './search.css'
})
export class Search {
  searchForm: FormGroup;
  results: CircleDTO[] = [];
  loading = false;
  error: string | null = null;
  genres: Genre[] = [];

  showFiltersModal = false;

  constructor(
    private fb: FormBuilder,
    private searchService: SearchService,
    private circleService: CircleService
  ) {
    this.searchForm = this.fb.group({
      motCle: [''],
      date: [''],
      nbMembresMin: [null],
      genreId: [null],
      estOuvert: [null],
      modeRencontre: this.fb.group({
        presentiel: [false],
        distanciel: [false]
      })
    });

    this.loadGenres();
    this.loadAllCircles();
  }

  loadAllCircles() {
    this.loading = true;
    this.error = null;

    this.searchService.searchCircles({}).subscribe({
      next: data => {
        this.results = data;
        this.loading = false;
      },
      error: err => {
        this.error = 'Erreur lors du chargement des cercles';
        this.loading = false;
      }
    });
  }

  loadGenres(): void {
    this.circleService.getGenres().subscribe({
      next: data => this.genres = data,
      error: err => console.error('Erreur chargement genres', err)
    });
  }

  openFilters() {
    this.showFiltersModal = true;
  }

  closeFilters() {
    this.showFiltersModal = false;
  }

  onSubmit() {
    if (this.searchForm.invalid) return;

    this.loading = true;
    this.error = null;

    const formValue = this.searchForm.value;

    const criteria: SearchCriteriaDTO = {
      motCle: formValue.motCle || null,
      genre: formValue.genreId ? this.getGenreNameById(formValue.genreId) : null,
      format: null,
      date: formValue.date || null,
      dateExacte: formValue.date || null,
      estOuvert: formValue.estOuvert === null ? null : formValue.estOuvert
    };

    if (formValue.modeRencontre.presentiel && !formValue.modeRencontre.distanciel) {
      criteria.format = 'PRESENTIEL';
    } else if (!formValue.modeRencontre.presentiel && formValue.modeRencontre.distanciel) {
      criteria.format = 'ENLIGNE';
    } else {
      criteria.format = null; // Pas de filtre ou les deux cochés
    }

    this.searchService.searchCircles(criteria).subscribe({
      next: data => {
        this.results = data;
        this.loading = false;
        this.closeFilters();
      },
      error: err => {
        this.error = 'Erreur lors de la recherche';
        this.loading = false;
      }
    });
  }

  getGenreNameById(id: number | string): string | null {
    const idNum = typeof id === 'string' ? parseInt(id, 10) : id;
    const genre = this.genres.find(g => {
      const genreIdNum = typeof g.id === 'string' ? parseInt(g.id, 10) : g.id;
      return genreIdNum === idNum;
    });
    return genre ? genre.nom : null;
  }

  getModeRencontreIcon(mode?: string): string {
    if (!mode) return 'assets/images/icons8/point-carte.png';
    const m = mode.trim().toUpperCase().replace('_', '');
    return m === 'PRESENTIEL'
      ? 'assets/images/icons8/point-carte.png'
      : 'assets/images/icons8/moniteur.png';
  }

  getModeRencontreLabel(mode?: string): string {
    if (!mode) return 'Inconnu';
    return mode.trim().toUpperCase() === 'ENLIGNE' ? 'En ligne' : 'Présentiel';
  }
}
