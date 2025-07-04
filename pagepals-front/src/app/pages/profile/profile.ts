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

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    const storedUserId = localStorage.getItem('userId');
    if (!storedUserId) {
      this.router.navigate(['/login']);
      return;
    }
    this.userId = Number(storedUserId);
    console.log('UserId récupéré :', this.userId);

    forkJoin({
      userProfile: this.userService.getUserProfile(this.userId),
      createdCircles: this.userService.getCreatedCircles(this.userId),
      joinedCircles: this.userService.getJoinedCircles(this.userId)
    }).subscribe({
      next: ({ userProfile, createdCircles, joinedCircles }) => {
        console.log('Données userProfile :', userProfile);
        console.log('Cercles créés :', createdCircles);
        console.log('Cercles rejoints :', joinedCircles);
        this.userProfile = userProfile;
        this.createdCircles = createdCircles;
        this.joinedCircles = joinedCircles;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement du profil:', err);
        this.errorMessage = 'Erreur lors du chargement du profil';
        this.loading = false;
      }
    });
  }

  get profilePhotoUrl(): string {
    return this.userProfile?.photoProfil
      ? `/api/user/photo/${this.userProfile?.photoProfil}`
      : 'assets/images/icons8/photo-profil.png';
  }


  onProposeCircle(): void {
    this.router.navigate(['/circle/create']);
  }

  onFindCircle(): void {
    this.router.navigate(['/circle/search']);
  }

  onModifyCircle(circleId: number): void {
    this.router.navigate([`/circle/edit/${circleId}`]);
  }

  onQuitCircle(circleId: number): void {
    // À implémenter selon service membership
  }
}
