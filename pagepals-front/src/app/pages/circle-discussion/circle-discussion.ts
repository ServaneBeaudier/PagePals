import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CircleService, MessageDTO } from '../../core/circle.service';
import { TokenStorage } from '../../core/token-storage';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-circle-discussion',
  imports: [FormsModule, ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './circle-discussion.html',
  styleUrl: './circle-discussion.css'
})
export class CircleDiscussion implements OnInit {
  circleId!: number;
  messages: MessageDTO[] = [];
  newMessageContent = '';

  constructor(
    private route: ActivatedRoute,
    private circleService: CircleService,
    private tokenStorage: TokenStorage,
    private router:Router,
  ) { }

  ngOnInit(): void {
    this.circleId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadMessages();
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

  getPhotoUrl(photoFilename?: string): string {
    if (photoFilename) {
      return `/api/user/photo/${encodeURIComponent(photoFilename)}`;
    }
    return 'assets/images/icons8/photo-profil.png';
  }

  goToCercle(id: number): void {
    this.router.navigate(['/circles', id]);
  }
}