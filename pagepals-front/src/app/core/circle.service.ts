import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type ModeRencontre = 'EN_LIGNE' | 'PRESENTIEL';

export interface CreateCircleDTO {
  nom: string;
  description: string;
  modeRencontre: ModeRencontre;
  dateRencontre: string;     // ISO datetime string
  nbMaxMembres?: number;
  genreIds: number[];
  lieuRencontre?: string;
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

@Injectable({
  providedIn: 'root'
})
export class CircleService {
  private apiUrl = '/api/circles';

  constructor(private http: HttpClient) { }

  createCircle(dto: CreateCircleDTO, token: string): Observable<void> {
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
}
