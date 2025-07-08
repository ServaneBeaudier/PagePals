import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AdresseDetails, CircleDTO, CircleService, Genre } from '../../core/circle.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../core/user.service';

@Component({
  selector: 'app-circle-detail',
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './circle-detail.html',
  styleUrl: './circle-detail.css'
})
export class CircleDetail implements OnInit {
  circleId!: number;
  circle?: CircleDTO;
  genres: Genre[] = [];
  createurPseudo: string = "";

  constructor(private route: ActivatedRoute,
    private circleService: CircleService,
    private router: Router,
    private userService: UserService) { }

  ngOnInit() {
    this.loadGenres();
    this.circleId = +this.route.snapshot.paramMap.get('id')!;
    this.loadCircle();
  }

  loadCircle() {
    this.circleService.getCircleById(this.circleId).subscribe({
      next: (data) => {
        this.circle = data;
        console.log('Données cercle:', this.circle);

        if (this.circle && this.circle.createurId) {
          this.userService.getUserProfile(this.circle.createurId).subscribe({
            next: user => this.createurPseudo = user.pseudo,
            error: err => this.createurPseudo = 'Utilisateur inconnu'
          });
        } else {
          this.createurPseudo = 'Utilisateur inconnu';
        }
      },
      error: (err) => {
        console.error('Erreur chargement cercle', err);
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

  getModeRencontreIcon(mode?: string): string {
    console.log('Appel getModeRencontreIcon avec mode:', mode);
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

  onModifyCircle(circleId: number): void {
    this.router.navigate([`/circles/edit/${circleId}`]);
  }

  getLieuRencontreDetails(): AdresseDetails | undefined {
    return this.circle?.lieuRencontreDetails;
  }
}
