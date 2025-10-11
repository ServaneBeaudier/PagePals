import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
  HttpClient
} from '@angular/common/http';
import { Observable, catchError, switchMap, throwError, BehaviorSubject } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { TokenStorage } from './token-storage';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  constructor(private tokenStorage: TokenStorage, private http: HttpClient) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenStorage.getToken();

    // 🧩 Ajouter le token à la requête sauf pour /api/user/photo
    let authReq = req;
    if (token && !req.url.includes('/api/user/photo')) {
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(authReq).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          // 🔄 Token expiré -> tenter un refresh
          return this.handle401Error(authReq, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = this.tokenStorage.getRefreshToken();

      if (!refreshToken) {
        this.isRefreshing = false;
        return throwError(() => new Error('No refresh token available'));
      }

      // 🔁 Appel au endpoint /api/auth/refresh
      return this.http
        .post<any>('/api/auth/refresh', { refreshToken })
        .pipe(
          switchMap((response) => {
            // ✅ On sauvegarde les nouveaux tokens
            this.isRefreshing = false;
            this.tokenStorage.saveToken(response.token);
            this.tokenStorage.saveRefreshToken(response.refreshToken);
            this.refreshTokenSubject.next(response.token);

            // 🔄 On rejoue la requête d’origine
            const cloned = request.clone({
              setHeaders: { Authorization: `Bearer ${response.token}` }
            });
            return next.handle(cloned);
          }),
          catchError((err) => {
            this.isRefreshing = false;
            this.tokenStorage.signOut();
            return throwError(() => err);
          })
        );
    } else {
      // Si un refresh est déjà en cours, on attend qu’il se termine
      return this.refreshTokenSubject.pipe(
        filter((token) => token !== null),
        take(1),
        switchMap((token) =>
          next.handle(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }))
        )
      );
    }
  }
}
