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

    const userIdNum = Number(this.currentUserId);

    this.circleService.getArchivedCircles().subscribe({
      next: circles => {
        const participantChecks$ = circles.map(circle =>
          this.membershipService.getParticipants(circle.id!).pipe(
            catchError(err => {
              console.error(`Erreur chargement participants cercle ${circle.id}`, err);
              return of([]);
            })
          )
        );

        forkJoin(participantChecks$).subscribe({
          next: participantsArrays => {
            circles.forEach((circle, i) => {
              const participants = participantsArrays[i];
              const isCreator = circle.createurId === userIdNum;
              const isParticipant = participants.some(p => Number(p.id) === userIdNum);
              console.log(`Circle ${circle.id} - isCreator: ${isCreator}, isParticipant: ${isParticipant}`);
            });

            this.archivedCircles = circles.filter((circle, i) => {
              const participants = participantsArrays[i];
              const isCreator = circle.createurId === userIdNum;
              const isParticipant = participants.some(p => Number(p.id) === userIdNum);
              return isCreator || isParticipant;
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
