import { Component, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { UserService } from '../../app/services/user.service';
import { UserBasicInfo } from '../../app/models/user-basic-info';
import { EnumOption } from '../../app/models/enum-options';
import { SavedRoute } from '../../app/models/saved-route';
import { UserSavedGasStation } from '../../app/models/user-saved-gas-station';
import { FavouriteStationsSearchComponent } from './favourite-stations-search.component';
import { Router } from '@angular/router';
import { RouteNavigationService } from '../../app/services/route-navigations.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FavouriteStationsSearchComponent],
  template: `
    <div class="page">
      <h1>Mi perfil</h1>

      @if (loading()) {
        <p>Cargando datos del usuario…</p>
      }

      @if (error()) {
        <p class="error">{{ error() }}</p>
      }

      @if (user()) {
        <section class="card">
          <h2>Datos básicos</h2>
          <p><strong>Email:</strong> {{ user()!.email }}</p>
          <p><strong>Nombre:</strong> {{ user()!.name }}</p>
          <p><strong>Apellidos:</strong> {{ user()!.surname }}</p>
        </section>
      }

      <section class="card">
        <h2>Preferencias de usuario</h2>

        <form [formGroup]="preferencesForm" (ngSubmit)="saveUserPreferences()" class="grid">
          <label>
            Tema
            <select formControlName="theme">
              @for (theme of themes(); track theme.code) {
                <option [value]="theme.code">{{ theme.label }}</option>
              }
            </select>
          </label>

          <label>
            Idioma
            <select formControlName="language">
              @for (lang of languages(); track lang.code) {
                <option [value]="lang.code">{{ lang.label }}</option>
              }
            </select>
          </label>

          <button type="submit" [disabled]="preferencesForm.invalid || savingPreferences()">
            Guardar preferencias
          </button>
        </form>

        @if (preferencesMessage()) {
          <p class="ok">{{ preferencesMessage() }}</p>
        }
      </section>

      <section class="card">
        <h2>Rutas guardadas</h2>

        @if (savedRoutes().length === 0) {
          <p>No tienes rutas guardadas.</p>
        }

        <div class="list">
          @for (route of savedRoutes(); track route.routeId) {
            <article class="item">
              <div>
                <strong>{{ route.name }}</strong>
                <p class="muted">
                  {{ route.points.length }} puntos
                </p>
              </div>

              <div class="actions">
                <button type="button" (click)="renameRoute(route)">Renombrar</button>
                <button type="button" (click)="deleteRoute(route.routeId)">Eliminar</button>
              </div>
            </article>
          }
        </div>
      </section>

      <app-favourite-stations-search
        (favouriteSaved)="reloadFavouriteStations()">
      </app-favourite-stations-search>

      <section class="card">
        <h2>Gasolineras favoritas</h2>

        @if (favouriteStations().length === 0) {
          <p>No tienes gasolineras favoritas.</p>
        }

        <div class="list">
          @for (station of favouriteStations(); track station.alias) {
            <article class="item">
              <div>
                <strong>{{ station.alias }}</strong>
                <p class="muted">{{ station.nombreEstacion }} · {{ station.marca }}</p>
                <p class="muted">{{ station.direccion }}</p>
              </div>

              <div class="actions">
                <button type="button" (click)="goToStation(station)">Ir</button>
                <button type="button" (click)="renameStation(station.alias)">Renombrar</button>
                <button type="button" (click)="deleteStation(station.alias)">Eliminar</button>
              </div>
            </article>
          }
        </div>
      </section>
    </div>
  `,
  styles: [`
    .page{
      max-width: 1100px;
      margin: 24px auto;
      padding: 0 16px 24px;
      display: grid;
      gap: 16px;
    }
    .card{
      border: 1px solid #ddd;
      border-radius: 12px;
      padding: 16px;
    }
    .grid{
      display:grid;
      gap:12px;
      max-width: 420px;
    }
    .list{
      display:grid;
      gap:12px;
    }
    .item{
      display:flex;
      justify-content:space-between;
      gap:16px;
      border:1px solid #eee;
      border-radius:10px;
      padding:12px;
      align-items:flex-start;
    }
    .actions{
      display:flex;
      gap:8px;
      flex-wrap:wrap;
    }
    .muted{
      margin:4px 0 0;
      color:#666;
      font-size:14px;
    }
    .error{
      color:#b00020;
    }
    .ok{
      color:green;
    }
    input, select, button{
      padding:8px;
      box-sizing:border-box;
    }
  `]
})
export class ProfilePageComponent {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly routeNavigation = inject(RouteNavigationService);

  loading = signal(true);
  error = signal<string | null>(null);

  savingPreferences = signal(false);
  preferencesMessage = signal<string | null>(null);

  user = signal<UserBasicInfo | null>(null);
  languages = signal<EnumOption[]>([]);
  themes = signal<EnumOption[]>([]);
  savedRoutes = signal<SavedRoute[]>([]);
  favouriteStations = signal<UserSavedGasStation[]>([]);

  preferencesForm = new FormGroup({
    theme: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    language: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  constructor() {
    this.loadAll();
  }

  loadAll() {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      user: this.userService.getCurrentUser(),
      languages: this.userService.getLanguages(),
      themes: this.userService.getThemes(),
      userPreferences: this.userService.getUserPreferences(),
      savedRoutes: this.userService.getSavedRoutes(),
      favouriteStations: this.userService.getFavouriteStations(),
    }).subscribe({
      next: ({ user, languages, themes, userPreferences, savedRoutes, favouriteStations }) => {
        this.user.set(user);
        this.languages.set(languages);
        this.themes.set(themes);
        this.savedRoutes.set(savedRoutes);
        this.favouriteStations.set(favouriteStations);

        this.preferencesForm.setValue({
          theme: userPreferences.theme ?? '',
          language: userPreferences.language ?? '',
        });

        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.message ?? 'No se pudieron cargar los datos del perfil.');
        this.loading.set(false);
      }
    });
  }

  saveUserPreferences() {
    if (this.preferencesForm.invalid) return;

    this.savingPreferences.set(true);
    this.preferencesMessage.set(null);

    const value = this.preferencesForm.getRawValue();

    this.userService.updateUserPreferences(value.theme, value.language).subscribe({
      next: () => {
        this.savingPreferences.set(false);
        this.preferencesMessage.set('Preferencias guardadas correctamente.');
      },
      error: (err) => {
        this.savingPreferences.set(false);
        this.preferencesMessage.set(err?.message ?? 'No se pudieron guardar las preferencias.');
      }
    });
  }

  deleteRoute(routeId: number) {
    const confirmed = window.confirm('¿Seguro que quieres eliminar esta ruta?');
    if (!confirmed) return;

    this.userService.deleteSavedRoute(routeId).subscribe({
      next: () => {
        this.savedRoutes.set(this.savedRoutes().filter(r => r.routeId !== routeId));
      },
      error: () => {
        window.alert('No se pudo eliminar la ruta.');
      }
    });
  }

  renameRoute(route: SavedRoute) {
    const newName = window.prompt('Nuevo nombre para la ruta:', route.name);
    if (!newName || !newName.trim()) return;

    this.userService.renameSavedRoute(route.routeId, newName.trim()).subscribe({
      next: (updated) => {
        this.savedRoutes.set(
          this.savedRoutes().map(r => r.routeId === route.routeId ? updated : r)
        );
      },
      error: () => {
        window.alert('No se pudo renombrar la ruta.');
      }
    });
  }

  deleteStation(alias: string) {
    const confirmed = window.confirm('¿Seguro que quieres eliminar esta gasolinera favorita?');
    if (!confirmed) return;

    this.userService.deleteFavouriteStation(alias).subscribe({
      next: () => {
        this.favouriteStations.set(this.favouriteStations().filter(s => s.alias !== alias));
      },
      error: () => {
        window.alert('No se pudo eliminar la gasolinera.');
      }
    });
  }

  renameStation(oldAlias: string) {
    const newAlias = window.prompt('Nuevo alias para la gasolinera:', oldAlias);
    if (!newAlias || !newAlias.trim()) return;

    this.userService.renameFavouriteStation(oldAlias, newAlias.trim()).subscribe({
      next: () => {
        this.favouriteStations.set(
          this.favouriteStations().map(s =>
            s.alias === oldAlias ? { ...s, alias: newAlias.trim() } : s
          )
        );
      },
      error: () => {
        window.alert('No se pudo renombrar la gasolinera.');
      }
    });
  }
  reloadFavouriteStations() {
  this.userService.getFavouriteStations().subscribe({
    next: (stations) => {
      this.favouriteStations.set(stations);
    },
    error: () => {
      window.alert('No se pudieron recargar las gasolineras favoritas.');
    }
  });
}
goToStation(station: UserSavedGasStation) {
  if (!station?.direccion) {
    window.alert('Esta gasolinera no tiene dirección disponible.');
    return;
  }

  this.routeNavigation.setPendingRoute({
    origin: '',
    destination: `${station.nombreEstacion}, ${station.direccion}, ${station.localidad}, ${station.provincia}, España`,
    waypoints: [],
    radius: 2,
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false,
    vehicleEmissionType: 'C',
    language: 'es',
  });

  this.router.navigateByUrl('/');
}
}