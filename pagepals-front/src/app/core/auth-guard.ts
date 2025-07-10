import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { TokenStorage } from './token-storage';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private tokenStorage: TokenStorage, private router: Router) { }

  canActivate(): boolean {
  const hasToken = !!this.tokenStorage.getToken();
  console.log('AuthGuard canActivate, token présent ? ', hasToken);
  if (hasToken) return true;
  this.router.navigate(['/access-denied']);
  return false;
}

}
