import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Circle, UserProfile, UserService } from '../../core/user.service';
import { forkJoin } from 'rxjs/internal/observable/forkJoin';

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

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    this.successMessage = navigation?.extras?.state?.['message'] ?? null;

    const storedUserId = localStorage.getItem('userId');
    if (!storedUserId) {
      this.router.navigate(['/login']);
      return;
    }
    this.userId = Number(storedUserId);

    forkJoin({
      userProfile: this.userService.getUserProfile(this.userId),
      createdCircles: this.userService.getCreatedCircles(this.userId),
      joinedCircles: this.userService.getJoinedCircles(this.userId)
    }).subscribe({
      next: ({ userProfile, createdCircles, joinedCircles }) => {
        console.log(userProfile);
        this.userProfile = userProfile;
        this.createdCircles = createdCircles.map(c => ({
          id: c.id,
          nom: c.nom,
          nbMaxMembres: c.nbMaxMembres,
          membersCount: c.membersCount,
          modeRencontre: c.modeRencontre,
          dateRencontre: c.dateRencontre,
          lieuRencontre: c.lieuRencontre,
          lienVisio: c.lienVisio
        }));

        this.joinedCircles = joinedCircles;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement du profil';
        this.loading = false;
      }
    });
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
    // À implémenter selon service membership
  }
}
