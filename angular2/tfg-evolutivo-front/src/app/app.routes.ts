import { Routes } from '@angular/router';
import { MapPageComponent } from '../features/map/map-page.component';
import { LoginComponent } from '../features/auth/login.component';
import { RegisterComponent } from '../features/auth/register.component';
import { ProfilePageComponent } from '../features/profile/profile-page.component';

export const routes: Routes = [
  { path: '', component: MapPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfilePageComponent },
];