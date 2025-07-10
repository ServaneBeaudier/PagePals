import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AdresseDetails, CircleDTO, CircleService, Genre, MessageDTO } from '../../core/circle.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../core/user.service';
import { TokenStorage } from '../../core/token-storage';
import { MembershipService, Participant } from '../../core/membership.service';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-circle-detail',
  imports: [ReactiveFormsModule, CommonModule, RouterModule, FormsModule],
  templateUrl: './circle-detail.html',
  styleUrl: './circle-detail.css'
})
export class CircleDetail implements OnInit {
  circleId!: number;
  circle?: CircleDTO;
  genres: Genre[] = [];
  createurPseudo: string = "";
  createurPhotoUrl?: string;
  createurPhotoFilename?: string;
  messages: MessageDTO[] = [];
  newMessageContent = '';
  participants: Participant[] = [];
  isUserParticipantFlag = false;
  showParticipantsDropdown = false;
  isCreator: boolean = false;
  currentUserId: number | null = null;
  showConfirmQuitPopup = false;
  circleIdToQuit: number | null = null;

  constructor(private route: ActivatedRoute,
    private circleService: CircleService,
    private router: Router,
    private userService: UserService,
    private tokenStorage: TokenStorage,
    private membershipService: MembershipService,
    private authService: AuthService) { }

  ngOnInit() {
    this.currentUserId = this.authService.getUserIdFromStorage();
    this.loadGenres();
    this.circleId = +this.route.snapshot.paramMap.get('id')!;
    this.loadCircle();
    this.loadMessages();
    this.loadParticipants();
  }

  loadCircle() {
    this.circleService.getCircleById(this.circleId).subscribe({
      next: (data) => {
        this.circle = data;

        this.isCreator = this.currentUserId === this.circle.createurId;

        if (this.circle && this.circle.createurId) {
          this.userService.getUserProfile(this.circle.createurId).subscribe({
            next: user => {
              this.createurPseudo = user.pseudo;
              this.createurPhotoFilename = user.photoProfil;
            },
            error: err => {
              this.createurPseudo = 'Utilisateur inconnu';
              this.createurPhotoFilename = undefined;
            }
          });
        } else {
          this.createurPseudo = 'Utilisateur inconnu';
          this.createurPhotoFilename = undefined;
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

  getAuthors(authors?: string[]): string {
    return authors?.join(', ') ?? '';
  }

  loadParticipants() {
    this.membershipService.getParticipants(this.circleId).subscribe({
      next: participants => {
        this.participants = participants;
        this.isUserParticipantFlag = this.participants.some(p => (p.userId ?? p.id) === this.currentUserId);

        // Trouver le créateur dans la liste des participants
        const createur = this.participants.find(p => p.userId === this.circle?.createurId);
        if (createur) {
          this.createurPhotoUrl = createur.photoProfil;
        }
      },
      error: err => {
        console.error('Erreur chargement participants', err);
      }
    });
  }

  toggleParticipantsDropdown() {
    this.showParticipantsDropdown = !this.showParticipantsDropdown;
  }

  getPhotoUrl(photoFilename?: string): string {
    if (photoFilename) {
      return `/api/user/photo/${encodeURIComponent(photoFilename)}`;
    }
    return 'assets/images/icons8/photo-profil.png';
  }

  joinCircle(): void {
    const token = this.tokenStorage.getToken();
    if (!token || !this.circle || !this.currentUserId) {
      alert('Vous devez être connecté pour rejoindre ce cercle.');
      return;
    }
    this.membershipService.inscrire(this.currentUserId, this.circle.id!, token).subscribe({
      next: () => {
        this.loadParticipants();
      },
      error: (err) => {
        console.error('Erreur lors de l\'inscription', err);
        alert('Erreur lors de l\'inscription. Veuillez réessayer.');
      }
    });
  }

  leaveCircle(): void {
    this.circleIdToQuit = this.circleId;
    this.showConfirmQuitPopup = true;
  }


  confirmQuitCircle(): void {
    if (!this.circleIdToQuit) return;

    const token = this.tokenStorage.getToken();
    if (!token) {
      alert('Vous devez être connecté pour quitter ce cercle.');
      this.showConfirmQuitPopup = false;
      return;
    }

    this.membershipService.desinscrire(this.currentUserId!, this.circleIdToQuit, token).subscribe({
      next: () => {
        alert('Vous avez quitté le cercle.');
        this.showConfirmQuitPopup = false;
        this.circleIdToQuit = null;
        this.router.navigate(['/profile']);
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

  isUserParticipant(): boolean {
    const found = this.participants.some(p => p.userId === this.currentUserId);
    console.log('isUserParticipant ?', found, 'currentUserId=', this.currentUserId, 'participants=', this.participants);
    return found;
  }


  loadMessages() {
    if (!this.circleId) return;
    this.circleService.getMessages(this.circleId).subscribe({
      next: (msgs) => this.messages = msgs,
      error: (err) => console.error('Erreur chargement messages', err)
    });
  }

  sendMessage() {
    if (!this.newMessageContent.trim()) return;
    const token = this.tokenStorage.getToken();
    if (!token) {
      alert('Vous devez être connecté pour envoyer un message');
      return;
    }
    const dto: MessageDTO = {
      circleId: this.circleId,
      auteurId: 0,
      contenu: this.newMessageContent.trim(),
    };
    this.circleService.sendMessage(this.circleId, dto, token).subscribe({
      next: () => {
        this.newMessageContent = '';
        this.loadMessages();
      },
      error: (err) => alert('Erreur lors de l\'envoi du message')
    });
  }

  //pour version mobile uniquement
  goToDiscussion(id: number): void {
    this.router.navigate(['/circles', id, 'discussion']);
  }


}
