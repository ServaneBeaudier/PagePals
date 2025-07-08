import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { CommonModule } from '@angular/common';
import { Footer } from "./shared/footer/footer";
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, CommonModule, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  isLoggedIn = false;

  hideNavbarRoutes = ['/', '/login', '/register'];

  constructor(public router: Router, private authService: AuthService) {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
    });
  }

  shouldShowNavbar(): boolean {
    return this.isLoggedIn && !this.hideNavbarRoutes.includes(this.router.url);
  }
}
