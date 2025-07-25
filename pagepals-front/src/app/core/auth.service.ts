import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { TokenStorage } from './token-storage';

export interface RegisterDTO {
  email: string;
  motDePasse: string;
  pseudo: string;
}

export interface LoginDTO {
  email: string;
  motDePasse: string;
}

export interface AuthResponseDTO {
  token: string;
  type: string;
  id: number;
  username: string;
  roles: string[];
}

export interface UpdateMailDTO {
  userId: number;
  newEmail: string;
}

export interface UpdatePasswordDTO {
  userId: number;
  oldPassword: string;
  newPassword: string;
}


@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = '/api/auth';

  private loggedIn: BehaviorSubject<boolean>;

  isLoggedIn$: Observable<boolean>;

  constructor(private http: HttpClient, private tokenStorage: TokenStorage) {
    // Initialise ici, après injection
    this.loggedIn = new BehaviorSubject<boolean>(!!this.tokenStorage.getToken());
    this.isLoggedIn$ = this.loggedIn.asObservable();
  }

  register(data: RegisterDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.baseUrl}/register`, data);
  }

  login(credentials: LoginDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.baseUrl}/login`, credentials).pipe(
      tap(response => {
        this.tokenStorage.saveToken(response.token);
        localStorage.setItem('userId', response.id.toString());
        localStorage.setItem('auth-user', JSON.stringify(response));
        this.loggedIn.next(true);
      })
    );
  }

  getEmail(userId: number): Observable<{ email: string }> {
    return this.http.get<{ email: string }>(`${this.baseUrl}/email?id=${userId}`);
  }


  updateEmail(data: UpdateMailDTO): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/update-email`, data);
  }

  updatePassword(data: UpdatePasswordDTO): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/update-password`, data);
  }

  cleanupAuthUser(userId: number): Observable<void> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.put<void>(`${this.baseUrl}/cleanup`, null, { params });
  }

  logout(): void {
    this.tokenStorage.signOut();
    localStorage.removeItem('auth-user');
    this.loggedIn.next(false);
  }

  getUserIdFromStorage(): number | null {
    const stored = localStorage.getItem('userId');
    return stored ? +stored : null;
  }
}
