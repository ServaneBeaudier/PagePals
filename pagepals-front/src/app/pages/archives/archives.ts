import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CircleDTO, CircleService, Genre } from '../../core/circle.service';
import { AuthService } from '../../core/auth.service';
import { MembershipService, Participant } from '../../core/membership.service';
import { UserService, UserProfile } from '../../core/user.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-archives',
  imports: [RouterModule, CommonModule],
  templateUrl: './archives.html',
  styleUrl: './archives.css'
})
export class Archives implements OnInit {
  archivedCircles: CircleDTO[] = [];
  genres: Genre[] = [];
  currentUserId: number | null = null;

  creatorPseudoMap = new Map<number, string>();

  constructor(
    private circleService: CircleService,
    private authService: AuthService,
    private membershipService: MembershipService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.currentUserId = this.authService.getUserIdFromStorage();
    this.loadGenres();
    this.loadArchivedCircles();
  }

  loadGenres(): void {
    this.circleService.getGenres().subscribe({
      next: data => {
        this.genres = data;
        console.log('Genres chargés:', this.genres);
      },
      error: err => {
        console.error('Erreur chargement genres', err);
      }
    });
  }


  loadArchivedCircles(): void {
    if (!this.currentUserId) {
      this.archivedCircles = [];
      return;
    }

    this.circleService.getArchivedCircles().subscribe({
      next: circles => {
        console.log('Cercles archivés reçus:', circles);
        // Filtrer pour garder seulement cercles où utilisateur est créateur ou participant
        const filteredCircles$ = circles.filter(c =>
          c.createurId === this.currentUserId
        );

        // Maintenant récupérer tous les participants pour chaque cercle filtré
        const participantChecks$ = filteredCircles$.map(circle =>
          this.membershipService.getParticipants(circle.id!).pipe(
            catchError(err => {
              console.error(`Erreur chargement participants cercle ${circle.id}`, err);
              return of([]);
            })
          )
        );

        forkJoin(participantChecks$).subscribe({
          next: participantsArrays => {
            // Filtrer encore selon si user est participant
            this.archivedCircles = filteredCircles$.filter((circle, i) => {
              const participants = participantsArrays[i];
              return participants.some(p => p.userId === this.currentUserId) || circle.createurId === this.currentUserId;
            });

            this.loadCreatorsPseudo();
          },
          error: err => {
            console.error('Erreur forkJoin participants', err);
          }
        });
      },
      error: err => {
        console.error('Erreur chargement cercles archivés', err);
      }
    });
  }

  loadCreatorsPseudo(): void {
    const creatorIds = Array.from(new Set(this.archivedCircles.map(c => c.createurId))).filter(id => id != null);

    const userProfiles$ = creatorIds.map(id =>
      this.userService.getUserProfile(id!).pipe(
        catchError(err => {
          console.error(`Erreur chargement profil user ${id}`, err);
          return of({ id, pseudo: 'Inconnu' } as UserProfile);
        })
      )
    );

    forkJoin(userProfiles$).subscribe({
      next: profiles => {
        profiles.forEach(p => {
          this.creatorPseudoMap.set(p.userId, p.pseudo);
        });
      },
      error: err => {
        console.error('Erreur forkJoin profils', err);
      }
    });

  }

  getCreatorPseudo(createurId?: number): string {
    if (!createurId) return 'Inconnu';
    const pseudo = this.creatorPseudoMap.get(createurId) ?? 'Inconnu';
    return pseudo;
  }

  getGenreNameById(id: number): string {
    const genre = this.genres.find(g => g.id === id);
    console.log('Recherche genre id:', id, '=>', genre);
    return genre ? genre.nom : 'Inconnu';
  }
}
