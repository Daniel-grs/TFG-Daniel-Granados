import { Injectable, computed, signal } from '@angular/core';

type Lang = 'es' | 'en';

const translations = {
  es: {
    home: 'Inicio',
    login: 'Login',
    register: 'Registro',
    profile: 'Mi perfil',
    logout: 'Cerrar sesión',
    saveRoute: 'Guardar ruta',
    favouriteStations: 'Gasolineras favoritas',
    savedRoutes: 'Rutas guardadas',
    search: 'Buscar',
    clear: 'Limpiar',
  },
  en: {
    home: 'Home',
    login: 'Login',
    register: 'Register',
    profile: 'My profile',
    logout: 'Log out',
    saveRoute: 'Save route',
    favouriteStations: 'Favourite gas stations',
    savedRoutes: 'Saved routes',
    search: 'Search',
    clear: 'Clear',
  }
} as const;

@Injectable({ providedIn: 'root' })
export class I18nService {
  private readonly _lang = signal<Lang>('es');
  readonly lang = this._lang.asReadonly();

  setLanguage(language: string) {
    const lower = (language ?? '').toLowerCase();
    this._lang.set(lower.includes('en') ? 'en' : 'es');
  }

  t(key: keyof typeof translations.es) {
    return translations[this._lang()][key];
  }
}