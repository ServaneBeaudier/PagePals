import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { CircleDTO, CircleService, Genre } from '../../core/circle.service';
import { MembershipService } from '../../core/membership.service';
import { RouterModule } from '@angular/router';
import { UserService } from '../../core/user.service';
import { forkJoin, of } from 'rxjs';


type Tab = 'general' | 'mine';

@Component({
  selector: 'app-calendar',
  imports: [CommonModule, RouterModule],
  templateUrl: './calendar.html',
  styleUrl: './calendar.css'
})
export class Calendar {
  today = new Date();
  viewDate = new Date(this.today.getFullYear(), this.today.getMonth(), 1);
  selectedDate: Date | null = null;
  selectedTab: Tab = 'general';
  currentUserId: number | null = null;

  daysInMonth: (Date | null)[] = [];
  weekdays = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];

  circles: CircleDTO[] = [];
  genres: Genre[] = [];

  // Map cercleId -> nombre participants
  participantsCountMap = new Map<number, number>();

  constructor(
    private authService: AuthService,
    private circleService: CircleService,
    private membershipService: MembershipService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.currentUserId = this.authService.getUserIdFromStorage();
    this.generateDaysInMonth();
    this.loadGenres();
    this.loadCircles();
  }

  generateDaysInMonth(): void {
    const year = this.viewDate.getFullYear();
    const month = this.viewDate.getMonth();
    const numDays = new Date(year, month + 1, 0).getDate();
    const firstDay = new Date(year, month, 1).getDay();
    const paddingDays = firstDay === 0 ? 6 : firstDay - 1;
    this.daysInMonth = [];

    for (let i = 0; i < paddingDays; i++) {
      this.daysInMonth.push(null);
    }

    for (let day = 1; day <= numDays; day++) {
      this.daysInMonth.push(new Date(year, month, day));
    }
  }

  switchTab(tab: Tab): void {
    if (this.selectedTab !== tab) {
      this.selectedTab = tab;
      this.selectedDate = null;
      this.loadCircles();
    }
  }

  selectDate(date: Date): void {
    this.selectedDate = date;
  }

  loadGenres(): void {
    this.circleService.getGenres().subscribe({
      next: data => this.genres = data,
      error: err => console.error('Erreur chargement genres', err)
    });
  }

  loadCircles(): void {
    if (this.selectedTab === 'mine' && this.currentUserId) {
      forkJoin({
        created: this.userService.getCreatedCircles(this.currentUserId),
        joined: this.userService.getJoinedCircles(this.currentUserId)
      }).subscribe({
        next: ({ created, joined }) => {
          console.log('Cercles créés reçus:', created);
          console.log('Cercles rejoints reçus:', joined);

          // Mapper Circle en CircleDTO partiel
          const mapToDTO = (c: any): CircleDTO => ({
            id: c.id,
            nom: c.nom,
            description: c.description || '',
            modeRencontre: c.modeRencontre,
            dateRencontre: c.dateRencontre,
            nbMaxMembres: c.nbMaxMembres,
            genreIds: c.genreIds || [],
            createurId: c.createurId
          });

          const createdCircles = created.map(mapToDTO);
          const joinedCircles = joined.map(mapToDTO);

          // Fusionner et éviter doublons
          const map = new Map<number, CircleDTO>();

          createdCircles.forEach(c => map.set(c.id!, c));
          joinedCircles.forEach(c => {
            if (!map.has(c.id!)) {
              map.set(c.id!, c);
            }
          });

          let allCircles = Array.from(map.values());
          allCircles = allCircles.filter(c => !c.isArchived);
          this.circles = allCircles;

          // Afficher la correspondance genreIds -> noms (d’après this.genres)
          this.circles.forEach(circle => {
            const genreNoms = circle.genreIds?.map(id => {
              const g = this.genres.find(genre => genre.id === id);
              return g ? g.nom : 'Inconnu';
            }) || [];
            console.log(`Cercle ${circle.nom} genres:`, genreNoms);
          });

          this.loadParticipantsCount();
        },
        error: err => {
          console.error('Erreur chargement cercles créés et rejoints', err);
          this.circles = [];
        }
      });
    } else {
      this.circleService.getActiveCircles().subscribe({
        next: data => {
          this.circles = data;
          console.log('Cercles généraux reçus:', data);
          this.loadParticipantsCount();
        },
        error: err => {
          console.error('Erreur chargement cercles', err);
          this.circles = [];
        }
      });
    }
  }


  loadParticipantsCount(): void {
    this.participantsCountMap.clear();

    this.circles.forEach(circle => {
      this.membershipService.countMembers(circle.id!).subscribe({
        next: count => this.participantsCountMap.set(circle.id!, count),
        error: err => {
          console.error('Erreur chargement participants pour cercle', circle.id, err);
          this.participantsCountMap.set(circle.id!, 0);
        }
      });
    });
  }

  getGenreNameById(id: number): string {
    const genre = this.genres.find(g => g.id === id);
    return genre ? genre.nom : 'Inconnu';
  }

  getParticipantsCount(circleId: number): number {
    return this.participantsCountMap.get(circleId) ?? 0;
  }

  getCirclesForSelectedDate(): CircleDTO[] {
    if (!this.selectedDate) {
      return this.circles;
    }

    const filtered = this.circles.filter(c => {
      const cDate = new Date(c.dateRencontre);
      return (
        cDate.getFullYear() === this.selectedDate!.getFullYear() &&
        cDate.getMonth() === this.selectedDate!.getMonth() &&
        cDate.getDate() === this.selectedDate!.getDate()
      );
    });

    if (filtered.length > 0) {
      return filtered;
    }

    const next7Days: CircleDTO[] = [];
    for (let i = 1; i <= 7; i++) {
      const nextDate = new Date(this.selectedDate!);
      nextDate.setDate(nextDate.getDate() + i);

      this.circles.forEach(c => {
        const cDate = new Date(c.dateRencontre);
        if (
          cDate.getFullYear() === nextDate.getFullYear() &&
          cDate.getMonth() === nextDate.getMonth() &&
          cDate.getDate() === nextDate.getDate()
        ) {
          if (!next7Days.some(existing => existing.id === c.id)) {
            next7Days.push(c);
          }
        }
      });
    }

    return next7Days;
  }

  // Icônes et labels pour mode rencontre (comme dans CircleDetail)
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

  previousMonth(): void {
    const year = this.viewDate.getFullYear();
    const month = this.viewDate.getMonth();
    this.viewDate = new Date(year, month - 1, 1);
    this.generateDaysInMonth();
    this.selectedDate = null;
  }

  nextMonth(): void {
    const year = this.viewDate.getFullYear();
    const month = this.viewDate.getMonth();
    this.viewDate = new Date(year, month + 1, 1);
    this.generateDaysInMonth();
    this.selectedDate = null;
  }

  hasCircleOnDate(date: Date): boolean {
    return this.circles.some(c => {
      const cDate = new Date(c.dateRencontre);
      return (
        cDate.getFullYear() === date.getFullYear() &&
        cDate.getMonth() === date.getMonth() &&
        cDate.getDate() === date.getDate()
      );
    });
  }

  getGroupedCircles() {
    const map = new Map<string, CircleDTO[]>();

    this.getCirclesForSelectedDate().forEach(circle => {
      const cDate = new Date(circle.dateRencontre);
      const key = cDate.toISOString().substring(0, 10); // 'YYYY-MM-DD'

      if (!map.has(key)) {
        map.set(key, []);
      }
      map.get(key)!.push(circle);
    });

    return Array.from(map.entries())
      .sort(([dateA], [dateB]) => new Date(dateA).getTime() - new Date(dateB).getTime())
      .map(([dateStr, circles]) => {
        const date = new Date(dateStr);
        const day = date.getDate();
        const monthName = date.toLocaleString('fr-FR', { month: 'long' });
        const dayName = date.toLocaleString('fr-FR', { weekday: 'long' });
        return {
          dateLabel: ` ${day} ${monthName}`,
          dayName,
          circles
        };
      });
  }




}
