import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-navbar',
  standalone:true,
  imports: [RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

  pageTitle: string = 'PagePals';

  private routeTitles: { [key: string]: string } = {
    '/': 'Accueil',
    '/contact': 'Contactez-nous',
    '/confidentialite': 'Confidentialité',
    '/profile': 'Mon profil',
    '/circles': 'Cercles littéraires',
    'profile/edit': 'Mon compte',
    // ajoute ici toutes tes routes importantes et titres associés
  };

  constructor(private router: Router, private authService: AuthService) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.pageTitle = this.routeTitles[event.urlAfterRedirects] || 'PagePals';
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
