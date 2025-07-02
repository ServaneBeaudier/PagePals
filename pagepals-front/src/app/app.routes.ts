import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Profile } from './pages/profile/profile';
import { Circles } from './pages/circles/circles';
import { CircleDetail } from './pages/circle-detail/circle-detail';
import { CreateCircle } from './pages/create-circle/create-circle';
import { Confidentialite } from './pages/confidentialite/confidentialite';
import { Contact } from './pages/contact/contact';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'profile', component: Profile },
  { path: 'circles', component: Circles },
  { path: 'circles/:id', component: CircleDetail },
  { path: 'circles/create', component: CreateCircle },
  { path: 'confidentialite', component: Confidentialite },
  { path: 'contact', component: Contact },
  { path: '**', redirectTo: '' } // redirection en cas de route inconnue
];
