import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { AuthStateService } from './services/auth-state.service';
import { AuthService } from './services/auth.service';
import { MapStateService } from './services/map-state.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  template: `
    <header style="padding:12px; border-bottom:1px solid #ddd; display:flex; gap:12px; align-items:center;">
      <a routerLink="/">Inicio</a>
      <a routerLink="/login">Login</a>
      <a routerLink="/register">Registro</a>
      @if (authState.isAuthenticated()) {
         <a routerLink="/profile">Mi perfil</a>
      }

      <span style="margin-left:auto;">{{ authState.statusText() }}</span>

      @if (authState.isAuthenticated()) {
        <button type="button" (click)="logout()">Cerrar sesión</button>
      }
    </header>

    <router-outlet></router-outlet>
  `,
})
export class App {
  readonly authState = inject(AuthStateService);
  private readonly authService = inject(AuthService);
  private readonly mapState = inject(MapStateService);

  constructor() {
    this.authState.checkSession();
  }

  logout() {
  this.authService.logout().subscribe({
    next: () => {
      this.authState.markLoggedOut();
      this.mapState.clear();
    },
    error: () => {
      this.authState.markLoggedOut();
      this.mapState.clear();
    },
  });
}
}