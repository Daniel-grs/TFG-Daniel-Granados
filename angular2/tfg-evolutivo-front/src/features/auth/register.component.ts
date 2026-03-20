import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../app/services/auth.service';


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-card">
      <h2>Registro</h2>

      <form [formGroup]="form" (ngSubmit)="submit()">
        <label>
          Nombre
          <input type="text" formControlName="name" />
        </label>

        <label>
          Apellidos
          <input type="text" formControlName="surname" />
        </label>

        <label>
          Email
          <input type="email" formControlName="email" />
        </label>

        <label>
          Contraseña
          <input type="password" formControlName="password" />
        </label>

        <label>
          Confirmar contraseña
          <input type="password" formControlName="passwordConfirmation" />
        </label>

        <button type="submit" [disabled]="form.invalid || loading()">
          Crear cuenta
        </button>
      </form>

      @if (success()) {
        <p class="ok">{{ success() }}</p>
      }

      @if (error()) {
        <p class="error">{{ error() }}</p>
      }

      <p>
        ¿Ya tienes cuenta?
        <a routerLink="/login">Inicia sesión</a>
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
    .ok{
      color:green;
    }
  `]
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(2)] }),
    surname: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(2)] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(4)] }),
    passwordConfirmation: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(4)] }),
  });

  submit() {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);

    const raw = this.form.getRawValue();

    if (raw.password !== raw.passwordConfirmation) {
      this.loading.set(false);
      this.error.set('Las contraseñas no coinciden.');
      return;
    }

    this.authService.register({
      email: raw.email.trim(),
      password: raw.password,
      passwordConfirmation: raw.passwordConfirmation,
      name: raw.name.trim(),
      surname: raw.surname.trim(),
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set('Usuario creado correctamente.');
        setTimeout(() => this.router.navigateByUrl('/login'), 800);
      },
      error: (err) => {
        this.loading.set(false);

        if (err?.status === 400) {
          this.error.set('No se pudo registrar. Revisa los datos o si el usuario ya existe.');
          return;
        }

        this.error.set('Error al registrar usuario.');
      }
    });
  }
}