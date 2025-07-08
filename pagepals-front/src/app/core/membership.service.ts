import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Participant {
  userId: number;
  pseudo: string;
  photoProfil?: string;
  // ajoute d’autres champs utiles si besoin
}

@Injectable({
  providedIn: 'root'
})
export class MembershipService {

  private apiUrl = '/api/memberships';

  constructor(private http: HttpClient) {}

  getParticipants(circleId: number): Observable<Participant[]> {
    return this.http.get<Participant[]>(`${this.apiUrl}/circle/${circleId}/participants`);
  }

  inscrire(userId: number, circleId: number, token: string): Observable<void> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.post<void>(`${this.apiUrl}/inscription`, { userId, circleId }, { headers });
  }

  desinscrire(userId: number, circleId: number, token: string): Observable<void> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.delete<void>(`${this.apiUrl}/desinscrire`, { body: { userId, circleId }, headers });
  }

  countMembers(circleId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/${circleId}`);
  }
}
