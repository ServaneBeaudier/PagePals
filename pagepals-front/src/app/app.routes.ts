import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Profile } from './pages/profile/profile';
import { Circles } from './pages/circles/circles';
import { CircleDetail } from './pages/circle-detail/circle-detail';
import { CreateCircle } from './pages/create-circle/create-circle';
import { Confidentialite } from './pages/confidentialite/confidentialite';
import { Contact } from './pages/contact/contact';
import { NotFound } from './pages/not-found/not-found';
import { Landing } from './pages/landing/landing';
import { AuthGuard } from './core/auth-guard';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'profile', component: Profile, canActivate: [AuthGuard]},
  { path: 'circles', component: Circles },
  { path: 'circles/:id', component: CircleDetail },
  { path: 'circles/create', component: CreateCircle, canActivate: [AuthGuard] },
  { path: 'confidentialite', component: Confidentialite },
  { path: 'contact', component: Contact },
  { path: '**', component: NotFound }
];
