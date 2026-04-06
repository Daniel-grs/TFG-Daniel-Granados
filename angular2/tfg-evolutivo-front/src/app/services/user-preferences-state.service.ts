import { Injectable, inject, signal } from '@angular/core';
import { UserService } from './user.service';
import { UserPreferences } from '../models/user-preferences';
import { I18nService } from './i18n.service';

@Injectable({ providedIn: 'root' })
export class UserPreferencesStateService {
  private readonly userService = inject(UserService);

  private readonly _preferences = signal<UserPreferences | null>(null);
  readonly preferences = this._preferences.asReadonly();
  private readonly i18n = inject(I18nService);

  loadAndApplyPreferences() {
    this.userService.getUserPreferences().subscribe({
      next: (prefs) => {
        this._preferences.set(prefs);
        this.applyPreferences(prefs);
      },
      error: () => {
      }
    });
  }

  setPreferences(prefs: UserPreferences) {
    this._preferences.set(prefs);
    this.applyPreferences(prefs);
  }

  clearPreferences() {
    this._preferences.set(null);
    document.documentElement.classList.remove('dark-theme');
    document.documentElement.classList.remove('light-theme');
    document.documentElement.classList.add('light-theme');
    document.documentElement.lang = 'es';
  }

  private applyPreferences(prefs: UserPreferences) {
    this.applyTheme(prefs.theme);
    this.applyLanguage(prefs.language);
  }

  private applyTheme(theme: string) {
    const root = document.documentElement;

    root.classList.remove('dark-theme');
    root.classList.remove('light-theme');

    if ((theme ?? '').toUpperCase().includes('DARK') || (theme ?? '').toUpperCase().includes('OSCURO')) {
      root.classList.add('dark-theme');
      return;
    }

    root.classList.add('light-theme');
  }

  private applyLanguage(language: string) {
    const lang = (language ?? '').toLowerCase();

    if (lang.includes('en')) {
      document.documentElement.lang = 'en';
      this.i18n.setLanguage('en');
      return;
    }

    document.documentElement.lang = 'es';
    this.i18n.setLanguage('es');
  }
}