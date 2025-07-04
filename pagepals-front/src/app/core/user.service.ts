import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Circle {
  id: number;
  nom: string;            
  nbMaxMembres: number;
  membersCount: number;
  modeRencontre: string;  // ex: 'Présentiel' ou 'En ligne'
  dateRencontre: string;  // ISO string
  lieuRencontre?: string;
  lienVisio?: string;
}

export interface UserProfile {
  id : number;
  pseudo: string;
  photoProfil?: string;
  bio?: string;
  dateInscription: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUser = '/api/user';
  private apiCircle = '/api/circles';
  private apiMembership = '/api/memberships';

  constructor(private http: HttpClient) {}

  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUser}/infos?id=${userId}`);
  }

  getCreatedCircles(userId: number): Observable<Circle[]> {
    return this.http.get<Circle[]>(`${this.apiCircle}/created-by/${userId}`);
  }

  getJoinedCircles(userId: number): Observable<Circle[]> {
    return this.http.get<Circle[]>(`${this.apiMembership}/by-user/${userId}`);
  }
}
