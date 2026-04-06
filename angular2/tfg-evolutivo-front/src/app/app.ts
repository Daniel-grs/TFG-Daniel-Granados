import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthStateService } from './services/auth-state.service';
import { AuthService } from './services/auth.service';
import { MapStateService } from './services/map-state.service';
import { UserPreferencesStateService } from './services/user-preferences-state.service';
import { I18nService } from './services/i18n.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  template: `
  <header style="padding:12px; border-bottom:1px solid #ddd; display:flex; gap:12px; align-items:center;">
    <a routerLink="/">{{ i18n.t('home') }}</a>

    @if (!authState.isAuthenticated()) {
      <a routerLink="/login">{{ i18n.t('login') }}</a>
      <a routerLink="/register">{{ i18n.t('register') }}</a>
    }

    @if (authState.isAuthenticated()) {
      <a routerLink="/profile">{{ i18n.t('profile') }}</a>
    }

    <span style="margin-left:auto;">{{ authState.statusText() }}</span>

    @if (authState.isAuthenticated()) {
      <button type="button" (click)="logout()">{{ i18n.t('logout') }}</button>
    }
  </header>

  <router-outlet></router-outlet>
`,
})
export class App {
  readonly authState = inject(AuthStateService);
  private readonly authService = inject(AuthService);
  private readonly mapState = inject(MapStateService);
  private readonly userPreferencesState = inject(UserPreferencesStateService);
  private readonly router = inject(Router);
  readonly i18n = inject(I18nService);

  constructor() {
    this.authState.checkSession();
    this.userPreferencesState.loadAndApplyPreferences();
  }

  logout() {
  this.authService.logout().subscribe({
    next: () => {
      this.authState.markLoggedOut();
      this.mapState.clear();
      this.userPreferencesState.clearPreferences();
      this.router.navigateByUrl('/');
    },
    error: () => {
      this.authState.markLoggedOut();
      this.mapState.clear();
      this.userPreferencesState.clearPreferences();
      this.router.navigateByUrl('/');
    },
  });
}
}