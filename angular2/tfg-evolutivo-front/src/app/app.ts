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
  <header class="app-header">
    <div class="page-shell app-header__inner">
      <div class="app-brand">
        <div class="app-brand__logo">R</div>
        <div>
          <strong>Route Planner</strong>
          <div class="app-brand__sub">Maps · Weather · Fuel</div>
        </div>
      </div>

      <nav class="app-nav">
        <a routerLink="/">{{ i18n.t('home') }}</a>

        @if (!authState.isAuthenticated()) {
          <a routerLink="/login">{{ i18n.t('login') }}</a>
          <a routerLink="/register">{{ i18n.t('register') }}</a>
        }

        @if (authState.isAuthenticated()) {
          <a routerLink="/profile">{{ i18n.t('profile') }}</a>
        }
      </nav>

      <div class="app-user">
        <span class="app-user__status">{{ authState.statusText() }}</span>

        @if (authState.isAuthenticated()) {
          <button class="secondary-btn" type="button" (click)="logout()">
            {{ i18n.t('logout') }}
          </button>
        }
      </div>
    </div>
  </header>

  <router-outlet></router-outlet>
`,
styles: [`
  .app-header{
    position: sticky;
    top: 0;
    z-index: 1000;
    background: var(--header-bg);
    backdrop-filter: var(--glass);
    -webkit-backdrop-filter: var(--glass);
    border-bottom: 1px solid var(--border);
  }

  .app-header__inner{
    min-height: 72px;
    display: grid;
    grid-template-columns: auto 1fr auto;
    align-items: center;
    gap: 20px;
  }

  .app-brand{
    display:flex;
    align-items:center;
    gap:12px;
    color: var(--text);
  }

  .app-brand__logo{
    width: 42px;
    height: 42px;
    border-radius: 14px;
    display:grid;
    place-items:center;
    color:white;
    font-weight:800;
    background: linear-gradient(135deg, var(--primary), var(--secondary));
    box-shadow: 0 10px 24px rgba(94, 114, 255, 0.28);
  }

  .app-brand__sub{
    font-size: 12px;
    color: var(--text-soft);
    margin-top: 2px;
  }

  .app-nav{
    display:flex;
    align-items:center;
    justify-content:center;
    gap:18px;
  }

  .app-nav a{
    color: var(--text);
    font-weight: 500;
  }

  .app-user{
    display:flex;
    align-items:center;
    gap:12px;
  }

  .app-user__status{
    color: var(--text-soft);
    font-size: 14px;
  }

  @media (max-width: 900px){
    .app-header__inner{
      grid-template-columns: 1fr;
      padding: 12px 0;
    }

    .app-nav{
      justify-content:flex-start;
      flex-wrap: wrap;
    }

    .app-user{
      justify-content:flex-start;
    }
  }
`]
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