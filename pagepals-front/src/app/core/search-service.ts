import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface SearchCriteriaDTO {
  motCle?: string | null;
  genre?: string | null;      // string car côté backend c’est String
  format?: string | null;     // string unique, pas tableau
  date?: string | null;       // ISO string, date simple
  dateExacte?: string | null; // ISO string, date simple (pas boolean)
  estOuvert?: boolean | null;
}

export interface CircleDTO {
  id: number;
  nom: string;
  dateRencontre: string;
  modeRencontre: string;
  genres: { id: number; nom: string }[];
  nbMaxMembres: number;
  nombreInscrits: number;
  estOuvert: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private apiUrl = '/api/search';

  constructor(private http: HttpClient) {}

  searchCircles(criteria: SearchCriteriaDTO): Observable<CircleDTO[]> {
    return this.http.post<CircleDTO[]>(this.apiUrl, criteria);
  }
}
