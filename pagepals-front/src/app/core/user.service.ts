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
  genreIds: number[];
}

export interface UserProfile {
  userId: number;
  id: number;
  pseudo: string;
  photoProfil?: string;
  bio?: string;
  dateInscription: string;
}

export interface UpdateUserProfileDTO {
  id: number;
  pseudo?: string;
  bio?: string;
  photoProfil?: string;
}


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUser = '/api/user';
  private apiCircle = '/api/circles';
  private apiMembership = '/api/memberships';

  constructor(private http: HttpClient) { }

  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUser}/infos?id=${userId}`);
  }

  getCreatedCircles(userId: number): Observable<Circle[]> {
    return this.http.get<Circle[]>(`${this.apiCircle}/created-by/${userId}`);
  }

  getJoinedCircles(userId: number): Observable<Circle[]> {
    return this.http.get<Circle[]>(`${this.apiMembership}/by-user/${userId}`);
  }

  updateUserProfile(dto: UpdateUserProfileDTO): Observable<void> {
    return this.http.put<void>(`${this.apiUser}/update`, dto);
  }

  uploadPhoto(file: File, userId: number): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId.toString());

    return this.http.put(`${this.apiUser}/update/photo`, formData, { responseType: 'text' });
  }

  deleteAccount(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUser}/cleanup`, { params: { userId: userId.toString() } });
  }

}
