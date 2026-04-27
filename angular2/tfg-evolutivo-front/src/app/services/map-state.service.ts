import { Injectable, computed, signal } from '@angular/core';
import { Coords } from '../models/coords';
import { RouteWeatherPoint } from '../models/route-weather';

export interface MapDataState {
  route: Coords[];
  gasStations: Coords[];
  weather: RouteWeatherPoint[] | null;

  distanceMeters: number | null;
  durationSeconds: number | null;

  loading: boolean;
  error: string | null;
}

const initialState: MapDataState = {
  route: [],
  gasStations: [],
  weather: null,

  distanceMeters: null,
  durationSeconds: null,

  loading: false,
  error: null,
};

@Injectable({ providedIn: 'root' })
export class MapStateService {
  private readonly _state = signal<MapDataState>(initialState);

  readonly state = this._state.asReadonly();

  readonly hasRoute = computed(() => this.state().route.length > 0);
  readonly gasCount = computed(() => this.state().gasStations.length);
 // readonly weatherCount = computed(() => this.state().weather?.length ?? 0);

  readonly routeDistanceKm = computed(() => {
    const meters = this.state().distanceMeters;
    return meters !== null ? (meters / 1000).toFixed(1) : null;
  });

  readonly routeDurationText = computed(() => {
    const seconds = this.state().durationSeconds;
    if (seconds === null) return null;

    const hours = Math.floor(seconds / 3600);
    const minutes = Math.round((seconds % 3600) / 60);

    if (hours > 0) {
      return `${hours} h ${minutes} min`;
    }

    return `${minutes} min`;
  });

  setLoading(loading: boolean) {
    this._state.update(s => ({ ...s, loading, error: null }));
  }

  setError(message: string) {
    this._state.update(s => ({ ...s, loading: false, error: message }));
  }

  setData(
    route: Coords[],
    gasStations: Coords[],
    weather: RouteWeatherPoint[] | null,
    distanceMeters: number | null = null,
    durationSeconds: number | null = null,
  ) {
    this._state.set({
      route: route ?? [],
      gasStations: gasStations ?? [],
      weather: weather ?? null,
      distanceMeters,
      durationSeconds,
      loading: false,
      error: null,
    });
  }

  setRoute(route: Coords[]) {
    this._state.update(s => ({
      ...s,
      route: route ?? [],
      loading: false,
      error: null,
    }));
  }

  setRouteMeta(distanceMeters: number | null, durationSeconds: number | null) {
    this._state.update(s => ({
      ...s,
      distanceMeters,
      durationSeconds,
    }));
  }

  clear() {
    this._state.set(initialState);
  }
}