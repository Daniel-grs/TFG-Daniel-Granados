import { Injectable, computed, inject, signal } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthStateService {
  private readonly authService = inject(AuthService);

  private readonly _isAuthenticated = signal(false);
  private readonly _loading = signal(false);

  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly loading = this._loading.asReadonly();

  readonly statusText = computed(() =>
    this._isAuthenticated() ? 'Sesión iniciada' : 'No autenticado'
  );

  checkSession() {
    this._loading.set(true);

    this.authService.check().subscribe({
      next: () => {
        this._isAuthenticated.set(true);
        this._loading.set(false);
      },
      error: () => {
        this._isAuthenticated.set(false);
        this._loading.set(false);
      }
    });
  }

  markLoggedIn() {
    this._isAuthenticated.set(true);
  }

  markLoggedOut() {
    this._isAuthenticated.set(false);
  }
}