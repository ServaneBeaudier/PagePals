import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { CommonModule } from '@angular/common';
import { Footer } from "./shared/footer/footer";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, CommonModule, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(public router: Router) {}

  hideNavbarRoutes = ['/login', '/register'];

  shouldShowNavbar(): boolean {
    return !this.hideNavbarRoutes.includes(this.router.url);
  }
}
