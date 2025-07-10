import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CircleDTO, CircleService, Genre } from '../../core/circle.service';

@Component({
  selector: 'app-archives',
  imports: [CommonModule, RouterModule],
  templateUrl: './archives.html',
  styleUrl: './archives.css'
})
export class Archives {
  archivedCircles: CircleDTO[] = [];
  genres: Genre[] = [];
  loading = false;
  error: string | null = null;

  constructor(private circleService: CircleService) { }

  ngOnInit(): void {
    this.loadGenres();
    this.loadArchivedCircles();
  }

  loadArchivedCircles() {
    this.loading = true;
    this.error = null;
    this.circleService.getArchivedCircles().subscribe({
      next: (data) => {
        this.archivedCircles = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = "Erreur lors du chargement des archives";
        this.loading = false;
      }
    });
  }

  loadGenres() {
    this.circleService.getGenres().subscribe({
      next: data => this.genres = data,
      error: err => console.error('Erreur chargement genres', err)
    });
  }

  getGenreNameById(id: number): string {
    const genre = this.genres.find(g => g.id === id);
    return genre ? genre.nom : 'Inconnu';
  }
}
