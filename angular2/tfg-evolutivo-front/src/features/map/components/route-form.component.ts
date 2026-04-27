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
    <form [formGroup]="form" (ngSubmit)="submit()" class="route-form">

  <div class="route-form-grid">
    <label>
      Origen
      <input formControlName="origin" placeholder="Madrid, España" />
    </label>

    <label>
      Destino
      <input formControlName="destination" placeholder="Valencia, España" />
    </label>
  </div>

  <div class="route-form-grid">
    <div class="route-form-full">
      <div class="waypoints-header">
        <strong>Waypoints</strong>
        <button class="secondary-btn" type="button" (click)="addWaypoint()">+ Añadir</button>
      </div>

      <div formArrayName="waypoints" class="waypoints-list">
        @for (w of waypoints.controls; track $index; let i = $index) {
          <div class="waypoint-row">
            <input [formControlName]="i" placeholder="Parada intermedia" />
            <button class="secondary-btn" type="button" (click)="removeWaypoint(i)">Quitar</button>
          </div>
        }
      </div>
    </div>

    <label>
      Radio gasolineras
      <input type="number" min="1" formControlName="radius" />
    </label>
  </div>

  <div class="checks-grid">
    <label class="check-card">
      <input type="checkbox" formControlName="optimizeWaypoints" />
      <span>Optimizar waypoints</span>
    </label>

    <label class="check-card">
      <input type="checkbox" formControlName="optimizeRoute" />
      <span>Optimizar ruta</span>
    </label>

    <label class="check-card">
      <input type="checkbox" formControlName="avoidTolls" />
      <span>Evitar peajes</span>
    </label>
  </div>

  <div class="route-form-actions">
    <button class="primary-btn" type="submit" [disabled]="form.invalid || state.state().loading">
      Buscar
    </button>

    <button class="secondary-btn" type="button" (click)="clear()" [disabled]="state.state().loading">
      Limpiar
    </button>

    @if (authState.isAuthenticated()) {
      <button
        class="secondary-btn"
        type="button"
        (click)="saveRoute()"
        [disabled]="!canSave() || saving() || state.state().loading"
      >
        Guardar ruta
      </button>
    }
  </div>

  @if (state.state().loading) {
    <p class="status-msg">Cargando…</p>
  }

  @if (state.state().error) {
    <p class="status-msg error">{{ state.state().error }}</p>
  }

  @if (saveSuccess()) {
    <p class="status-msg ok">{{ saveSuccess() }}</p>
  }

  @if (saveError()) {
    <p class="status-msg error">{{ saveError() }}</p>
  }
</form>
  `,
  styles: [`
  .route-form{
    display: grid;
    gap: 16px;
  }

  .route-form-grid{
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14px;
  }

  .route-form-full{
    display: grid;
    gap: 10px;
  }

  .waypoints-header{
    display:flex;
    align-items:center;
    justify-content:space-between;
    gap:12px;
  }

  .waypoints-list{
    display:grid;
    gap:10px;
  }

  .waypoint-row{
    display:flex;
    gap:10px;
  }

  .checks-grid{
    display:grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
  }

  .check-card{
    display:flex;
    align-items:center;
    gap:10px;
    padding: 12px 14px;
    border: 1px solid var(--border);
    border-radius: 14px;
    background: var(--input-bg);
    min-height: 52px;
  }

  .check-card input[type="checkbox"]{
    width: 18px;
    height: 18px;
    margin: 0;
    accent-color: var(--primary);
  }

  .check-card span{
    color: var(--text);
    font-weight: 500;
    line-height: 1.2;
  }

  .route-form-actions{
    display:flex;
    gap:10px;
    flex-wrap:wrap;
  }

  .route-form-actions button{
    min-width: 140px;
  }

  .status-msg{
    margin: 0;
    font-size: 14px;
  }

  .status-msg.error{
    color: #d44;
  }

  .status-msg.ok{
    color: #26a269;
  }

  @media (max-width: 900px){
    .route-form-grid,
    .checks-grid{
      grid-template-columns: 1fr;
    }

    .waypoint-row{
      flex-direction: column;
    }

    .route-form-actions{
      flex-direction: column;
    }

    .route-form-actions button{
      width: 100%;
    }
  }
`]
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
        avoidTolls: pending.avoidTolls ?? false
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
      summary: this.api.getRouteSummary(req),
    }).subscribe({
      next: ({ route, gas, weather, summary }) => {
        this.state.setData(
          route, 
          gas, 
          weather, 
          summary.distanceMeters ?? null,
          summary.durationSeconds ?? null
        );
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