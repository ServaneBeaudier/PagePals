import { Injectable } from '@angular/core';

const TOKEN_KEY = 'auth-token';
const REFRESH_TOKEN_KEY = 'refresh-token';
const USER_ID_KEY = 'userId';

@Injectable({
  providedIn: 'root'
})
export class TokenStorage {

  signOut(): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(REFRESH_TOKEN_KEY);
    window.localStorage.removeItem(USER_ID_KEY);
  }

  public saveToken(token: string): void {
    window.localStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return window.localStorage.getItem(TOKEN_KEY);
  }

  public saveRefreshToken(refreshToken: string): void {
    window.localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  }

  public getRefreshToken(): string | null {
    return window.localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  public saveUserId(id: number): void {
    window.localStorage.setItem(USER_ID_KEY, id.toString());
  }

  public getUserId(): number | null {
    const stored = window.localStorage.getItem(USER_ID_KEY);
    return stored ? +stored : null;
  }
}

