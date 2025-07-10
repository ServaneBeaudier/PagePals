import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type ModeRencontre = 'EN_LIGNE' | 'PRESENTIEL';

export interface AdresseDetails {
  shop?: string;
  houseNumber?: string;
  road?: string;
  postcode?: string;
  city?: string;
}

export interface CircleDTO {
  id?: number;
  nom: string;
  description: string;
  modeRencontre: ModeRencontre;
  dateRencontre: string;     // ISO datetime string
  nbMaxMembres?: number;
  genreIds: number[];
  lieuRencontre?: string;
  lieuRencontreDetails?: AdresseDetails;
  lienVisio?: string;
  createurId: number;
  livrePropose?: BookDTO;
}

export interface BookDTO {
  titre: string;
  auteurs: string[];
  isbn?: string;
  genre?: string;
  couvertureUrl?: string;
}

export interface SearchBookDTO {
  critereRecherche: string;
}

export interface Genre {
  id: number;
  nom: string;
}

export interface MessageDTO {
  id?: number;
  circleId: number;
  auteurId: number;
  contenu: string;
  dateEnvoi?: string;
  pseudoAuteur?: string;
  photoAuteur?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CircleService {
  private apiUrl = '/api/circles';

  constructor(private http: HttpClient) { }

  createCircle(dto: CircleDTO, token: string): Observable<void> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
    return this.http.post<void>(`${this.apiUrl}/create`, dto, { headers });
  }

  getGenres(): Observable<Genre[]> {
    return this.http.get<Genre[]>('/api/genres');
  }

  searchBooks(query: string): Observable<BookDTO[]> {
    return this.http.post<BookDTO[]>('/api/books/search', {
      critereRecherche: query
    });
  }

  getCircleById(id: number): Observable<CircleDTO> {
    return this.http.get<CircleDTO>(`${this.apiUrl}/${id}`);
  }

  getActiveCircles(): Observable<CircleDTO[]> {
    return this.http.get<CircleDTO[]>(`${this.apiUrl}/active`);
  }

  getArchivedCircles(): Observable<CircleDTO[]> {
    return this.http.get<CircleDTO[]>(`${this.apiUrl}/archived`);
  }

  updateCircle(dto: CircleDTO, token: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${dto.id}`, dto, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  deleteCircle(id: number, token: string): Observable<void> {
    const headers = { Authorization: `Bearer ${token}` };
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }

  getMessages(circleId: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(`${this.apiUrl}/${circleId}/messages`);
  }

  sendMessage(circleId: number, dto: MessageDTO, token: string): Observable<void> {
    const headers = { Authorization: `Bearer ${token}` };
    return this.http.post<void>(`${this.apiUrl}/${circleId}/messages`, dto, { headers });
  }

}
