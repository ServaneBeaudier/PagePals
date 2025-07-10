import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Circle, UserProfile, UserService } from '../../core/user.service';
import { forkJoin } from 'rxjs/internal/observable/forkJoin';
import { MembershipService } from '../../core/membership.service';
import { TokenStorage } from '../../core/token-storage';
import { catchError, map, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, RouterModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  userProfile: UserProfile | null = null;
  createdCircles: Circle[] = [];
  joinedCircles: Circle[] = [];
  loading = true;
  errorMessage: string | null = null;
  userId = 0;
  successMessage: string | null = null;
  showConfirmQuitPopup = false;
  circleIdToQuit: number | null = null;

  constructor(private userService: UserService,
    private router: Router,
    private membershipService: MembershipService,
    private tokenStorage: TokenStorage) { }

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    this.successMessage = navigation?.extras?.state?.['message'] ?? null;

    const storedUserId = localStorage.getItem('userId');
    if (!storedUserId) {
      this.router.navigate(['/login']);
      return;
    }
    this.userId = Number(storedUserId);
    this.loading = true;

    forkJoin({
      userProfile: this.userService.getUserProfile(this.userId),
      createdCircles: this.userService.getCreatedCircles(this.userId),
      joinedCircles: this.userService.getJoinedCircles(this.userId)
    }).pipe(
      switchMap(({ userProfile, createdCircles, joinedCircles }) => {
        // Fusionner created + joined sans doublons
        const circleMap = new Map<number, any>();
        createdCircles.forEach(circle => circleMap.set(circle.id, circle));
        joinedCircles.forEach(circle => {
          if (!circleMap.has(circle.id)) {
            circleMap.set(circle.id, circle);
          }
        });
        const allCircles = Array.from(circleMap.values());

        // Observable pour counts membres
        const countsObservables = allCircles.map(circle =>
          this.membershipService.countMembers(circle.id)
            .pipe(
              catchError(() => of(0))
            )
        );

        return forkJoin(countsObservables).pipe(
          map((counts: number[]) => {
            allCircles.forEach((circle: any, idx: number) => {
              circle.membersCount = counts[idx];
            });
            return { userProfile, createdCircles, joinedCircles, allCircles };
          })
        );
      })
    ).subscribe(
      ({ userProfile, createdCircles, joinedCircles, allCircles }) => {
        this.userProfile = userProfile;
        // Utiliser allCircles, mais répartir created et joined pour affichage si besoin
        this.createdCircles = allCircles.filter(c => createdCircles.some((cc: any) => cc.id === c.id));
        this.joinedCircles = allCircles.filter(c => joinedCircles.some((jc: any) => jc.id === c.id));
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Erreur lors du chargement du profil';
        this.loading = false;
      }
    );
  }


  get profilePhotoUrl(): string {
    if (this.userProfile?.photoProfil) {
      console.log(this.userProfile.photoProfil);
      return `/api/user/photo/${encodeURIComponent(this.userProfile.photoProfil)}`;
    }
    return 'assets/images/icons8/photo-profil.png';
  }

  formatMode(mode: string): string {
    if (!mode) return '';
    switch (mode.toUpperCase()) {
      case 'ENLIGNE': return 'En ligne';
      case 'PRESENTIEL': return 'Présentiel';
      default: return mode;
    }
  }

  onProposeCircle(): void {
    this.router.navigate(['/circles/create']);
  }

  onFindCircle(): void {
    this.router.navigate(['/search']);
  }

  onModifyCircle(circleId: number): void {
    this.router.navigate([`/circles/edit/${circleId}`]);
  }

  onQuitCircle(circleId: number): void {
    this.circleIdToQuit = circleId;
    this.showConfirmQuitPopup = true;
  }

  confirmQuitCircle(): void {
    if (!this.circleIdToQuit) return;

    const token = this.tokenStorage.getToken();
    if (!token) {
      alert('Vous devez être connecté pour quitter un cercle.');
      this.showConfirmQuitPopup = false;
      return;
    }

    this.membershipService.desinscrire(this.userId, this.circleIdToQuit, token).subscribe({
      next: () => {
        this.joinedCircles = this.joinedCircles.filter(c => c.id !== this.circleIdToQuit);
        alert('Vous avez quitté le cercle.');
        this.showConfirmQuitPopup = false;
        this.circleIdToQuit = null;
      },
      error: (err) => {
        console.error('Erreur lors de la désinscription', err);
        alert('Erreur lors de la désinscription. Veuillez réessayer.');
        this.showConfirmQuitPopup = false;
      }
    });
  }

  cancelQuit(): void {
    this.showConfirmQuitPopup = false;
    this.circleIdToQuit = null;
  }
}
