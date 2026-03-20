import { Component, effect, computed, inject, signal } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';

import { RouteApiService } from '../../../app/services/route-api.service';
import { MapStateService } from '../../../app/services/map-state.service';
import { RouteRequest } from '../../../app/models/route-request';
import { AuthStateService } from '../../../app/services/auth-state.service';
import { RouteNavigationService } from '../../../app/services/route-navigations.service';


@Component({
  selector: 'app-route-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="submit()" style="display:grid; gap: 10px;">
      <label>
        Origen
        <input formControlName="origin" placeholder="Madrid, España" />
      </label>

      <label>
        Destino
        <input formControlName="destination" placeholder="Valencia, España" />
      </label>

      <div>
        <div style="display:flex; align-items:center; justify-content:space-between;">
          <strong>Waypoints</strong>
          <button type="button" (click)="addWaypoint()">+ Añadir</button>
        </div>

        <div formArrayName="waypoints" style="display:grid; gap:8px; margin-top:8px;">
          @for (w of waypoints.controls; track $index; let i = $index) {
            <div style="display:flex; gap:8px;">
              <input [formControlName]="i" placeholder="Parada intermedia" />
              <button type="button" (click)="removeWaypoint(i)">Quitar</button>
            </div>
          }
        </div>
      </div>

      <label>
        Radio gasolineras
        <input type="number" min="1" formControlName="radius" />
      </label>

      <label style="display:flex; gap:8px; align-items:center;">
        <input type="checkbox" formControlName="optimizeWaypoints" />
        Optimizar waypoints
      </label>

      <label style="display:flex; gap:8px; align-items:center;">
        <input type="checkbox" formControlName="optimizeRoute" />
        Optimizar ruta
      </label>

      <label style="display:flex; gap:8px; align-items:center;">
        <input type="checkbox" formControlName="avoidTolls" />
        Evitar peajes
      </label>

      <label>
        Tipo de vehículo
        <select formControlName="vehicleEmissionType">
          <option value="C">C</option>
          <option value="GASOLINE">GASOLINE</option>
          <option value="DIESEL">DIESEL</option>
          <option value="HYBRID">HYBRID</option>
          <option value="ELECTRIC">ELECTRIC</option>
        </select>
      </label>

      <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:8px;">
        <button type="submit" [disabled]="form.invalid || state.state().loading">
          Buscar
        </button>

        <button type="button" (click)="clear()" [disabled]="state.state().loading">
          Limpiar
        </button>

        @if (authState.isAuthenticated()) {
          <button
            type="button"
            (click)="saveRoute()"
            [disabled]="!canSave() || saving() || state.state().loading"
          >
            Guardar ruta
          </button>
        }
      </div>

      @if (state.state().loading) {
        <p>Cargando…</p>
      }

      @if (state.state().error) {
        <p style="color:#b00020;">{{ state.state().error }}</p>
      }

      @if (saveSuccess()) {
        <p style="color:green;">{{ saveSuccess() }}</p>
      }

      @if (saveError()) {
        <p style="color:#b00020;">{{ saveError() }}</p>
      }
    </form>
  `,
})
export class RouteFormComponent {
  private readonly api = inject(RouteApiService);
  readonly state = inject(MapStateService);
  readonly authState = inject(AuthStateService);
  private readonly routeNavigation = inject(RouteNavigationService);

  saving = signal(false);
  saveSuccess = signal<string | null>(null);
  saveError = signal<string | null>(null);

  form = new FormGroup({
    origin: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(2)] }),
    destination: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(2)] }),
    waypoints: new FormArray<FormControl<string>>([]),
    radius: new FormControl(2, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
    optimizeWaypoints: new FormControl(false, { nonNullable: true }),
    optimizeRoute: new FormControl(false, { nonNullable: true }),
    avoidTolls: new FormControl(false, { nonNullable: true }),
    vehicleEmissionType: new FormControl<'C' | 'GASOLINE' | 'DIESEL' | 'HYBRID' | 'ELECTRIC'>('C', { nonNullable: true }),
  });

  constructor() {
    effect(() => {
      const pending = this.routeNavigation.pendingRoute();

      if (!pending) return;

      while (this.waypoints.length) {
        this.waypoints.removeAt(0);
      }

      (pending.waypoints ?? []).forEach(w => {
        this.waypoints.push(new FormControl(w, { nonNullable: true }));
      });

      this.form.patchValue({
        origin: pending.origin,
        destination: pending.destination,
        radius: pending.radius ?? 2,
        optimizeWaypoints: pending.optimizeWaypoints ?? false,
        optimizeRoute: pending.optimizeRoute ?? false,
        avoidTolls: pending.avoidTolls ?? false,
        vehicleEmissionType: (pending.vehicleEmissionType as 'C' | 'GASOLINE' | 'DIESEL' | 'HYBRID' | 'ELECTRIC') ?? 'C',
      });

      this.routeNavigation.clearPendingRoute();
    });
  }

  get waypoints() {
    return this.form.get('waypoints') as FormArray<FormControl<string>>;
  }

  canSave(): boolean {
  const v = this.form.getRawValue();
  return !!v.origin.trim() && !!v.destination.trim();
}

  addWaypoint() {
    this.waypoints.push(new FormControl('', { nonNullable: true }));
  }

  removeWaypoint(i: number) {
    this.waypoints.removeAt(i);
  }

  private buildRequest(): RouteRequest {
    const v = this.form.getRawValue();

    return {
      origin: v.origin.trim(),
      destination: v.destination.trim(),
      waypoints: (v.waypoints ?? []).map(w => w.trim()).filter(Boolean),
      radius: v.radius,
      optimizeWaypoints: v.optimizeWaypoints,
      optimizeRoute: v.optimizeRoute,
      avoidTolls: v.avoidTolls,
      vehicleEmissionType: v.vehicleEmissionType,
      language: 'es',
    };
  }

  submit() {
    if (this.form.invalid) return;

    this.saveSuccess.set(null);
    this.saveError.set(null);

    const req = this.buildRequest();
    this.state.setLoading(true);

    forkJoin({
      route: this.api.getPolylineCoords(req),
      gas: this.api.getGasStationsCoords(req),
      weather: this.api.getRouteWeather(req),
    }).subscribe({
      next: ({ route, gas, weather }) => {
        this.state.setData(route, gas, weather);
      },
      error: (err) => {
        this.state.setError(err?.message ?? 'Error llamando al backend');
      },
    });
  }

  saveRoute() {
    this.saveSuccess.set(null);
    this.saveError.set(null);

    if (!this.canSave()) {
      this.saveError.set('Debes indicar al menos origen y destino.');
      return;
    }

    const alias = window.prompt('Escribe un nombre para guardar esta ruta:');

    if (!alias || !alias.trim()) {
      return;
    }

    const req = this.buildRequest();
    this.saving.set(true);

    this.api.saveRoute(alias.trim(), req).subscribe({
      next: () => {
        this.saving.set(false);
        this.saveSuccess.set(`Ruta guardada como "${alias.trim()}"`);
      },
      error: (err) => {
        this.saving.set(false);
        this.saveError.set(err?.message ?? 'No se pudo guardar la ruta.');
      },
    });
  }

  clear() {
    this.form.reset({
      origin: '',
      destination: '',
      radius: 2,
      optimizeWaypoints: false,
      optimizeRoute: false,
      avoidTolls: false,
      vehicleEmissionType: 'C',
    });

    while (this.waypoints.length) {
      this.waypoints.removeAt(0);
    }

    this.saveSuccess.set(null);
    this.saveError.set(null);
    this.state.clear();
  }
}