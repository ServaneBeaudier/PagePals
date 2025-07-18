import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Profile } from './pages/profile/profile';
import { CircleDetail } from './pages/circle-detail/circle-detail';
import { CreateCircle } from './pages/create-circle/create-circle';
import { Confidentialite } from './pages/confidentialite/confidentialite';
import { Contact } from './pages/contact/contact';
import { NotFound } from './pages/not-found/not-found';
import { Landing } from './pages/landing/landing';
import { AuthGuard } from './core/auth-guard';
import { Editcircle } from './pages/editcircle/editcircle';
import { ProfileEdit } from './pages/profile-edit/profile-edit';
import { CircleDiscussion } from './pages/circle-discussion/circle-discussion';
import { Calendar } from './pages/calendar/calendar';
import { Search } from './pages/search/search';
import { Archives } from './pages/archives/archives';
import { AccessDenied } from './pages/access-denied/access-denied';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'access-denied', component: AccessDenied },
  { path: 'profile', component: Profile, canActivate: [AuthGuard]},
  { path: 'profile/edit', component: ProfileEdit, canActivate: [AuthGuard]},
  { path: 'circles/create', component: CreateCircle, canActivate: [AuthGuard] },
  { path: 'circles/edit/:id', component: Editcircle, canActivate: [AuthGuard] },
  { path: 'circles/:id', component: CircleDetail, canActivate: [AuthGuard] },
  { path: 'circles/:id/discussion', component: CircleDiscussion, canActivate: [AuthGuard] },
  { path: 'calendar', component: Calendar, canActivate: [AuthGuard]},
  { path: 'search', component: Search},
  { path: 'archives', component: Archives, canActivate: [AuthGuard]},
  { path: 'confidentialite', component: Confidentialite },
  { path: 'contact', component: Contact },
  { path: '**', component: NotFound }
];
