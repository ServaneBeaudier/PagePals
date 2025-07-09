import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { CircleDTO, CircleService, Genre } from '../../core/circle.service';
import { MembershipService } from '../../core/membership.service';
import { RouterModule } from '@angular/router';


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
    private membershipService: MembershipService
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

    // Jour de la semaine du 1er jour du mois (0=dimanche, 1=lundi, ..., 6=samedi)
    const firstDay = new Date(year, month, 1).getDay();

    // En Angular (fr), on veut la semaine qui commence lundi,
    // donc on ajuste pour que lundi = 0, dimanche = 6
    const paddingDays = firstDay === 0 ? 6 : firstDay - 1;

    this.daysInMonth = [];

    // Ajout des cases vides pour décaler le début du mois
    for (let i = 0; i < paddingDays; i++) {
      this.daysInMonth.push(null); // null = case vide
    }

    // Ajout des jours réels du mois
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
    this.circleService.getActiveCircles().subscribe({
      next: data => {
        this.circles = data;
        this.loadParticipantsCount();
      },
      error: err => {
        console.error('Erreur chargement cercles', err);
        this.circles = [];
      }
    });
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
