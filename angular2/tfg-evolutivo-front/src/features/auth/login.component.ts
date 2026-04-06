import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../app/services/auth.service';
import { AuthStateService } from '../../app/services/auth-state.service';
import { UserPreferencesStateService } from '../../app/services/user-preferences-state.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-card">
      <h2>Iniciar sesión</h2>

      <form [formGroup]="form" (ngSubmit)="submit()">
        <label>
          Email
          <input type="email" formControlName="user" />
        </label>

        <label>
          Contraseña
          <input type="password" formControlName="password" />
        </label>

        <button type="submit" [disabled]="form.invalid || loading()">
          Entrar
        </button>
      </form>

      @if (error()) {
        <p class="error">{{ error() }}</p>
      }

      <p>
        ¿No tienes cuenta?
        <a routerLink="/register">Regístrate</a>
      </p>
    </div>
  `,
  styles: [`
    .auth-card{
      max-width: 420px;
      margin: 40px auto;
      border: 1px solid #ddd;
      border-radius: 12px;
      padding: 16px;
      display: grid;
      gap: 12px;
    }
    form{
      display:grid;
      gap:12px;
    }
    input, button{
      width:100%;
      padding:8px;
      box-sizing:border-box;
    }
    .error{
      color:#b00020;
    }
  `]
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly authState = inject(AuthStateService);
  private readonly router = inject(Router);
  private readonly userPreferencesState = inject(UserPreferencesStateService);

  loading = signal(false);
  error = signal<string | null>(null);

  form = new FormGroup({
    user: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(4)] }),
  });

  submit() {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    const body = this.form.getRawValue();

    this.authService.login(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.authState.markLoggedIn();
        this.userPreferencesState.loadAndApplyPreferences();
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        this.loading.set(false);

        if (err?.status === 401) {
          this.error.set('Credenciales incorrectas.');
          return;
        }

        if (err?.status === 400) {
          this.error.set('Faltan datos obligatorios.');
          return;
        }

        this.error.set('No se pudo iniciar sesión.');
      }
    });
  }
}