import { Injectable, computed, signal } from '@angular/core';
import { Coords } from '../models/coords';
import { CoordsWithWeather } from '../models/coords-with-weather';
import { RouteWeatherPoint } from '../models/route-weather';

export interface MapDataState {
  route: Coords[];
  gasStations: Coords[];
  weather: RouteWeatherPoint[] | null;

  loading: boolean;
  error: string | null;
}

const initialState: MapDataState = {
  route: [],
  gasStations: [],
  weather: null,
  loading: false,
  error: null,
};

@Injectable({ providedIn: 'root' })
export class MapStateService {
  // Estado interno (signal)
  private readonly _state = signal<MapDataState>(initialState);

  // Estado público (solo lectura)
  readonly state = this._state.asReadonly();

  // Selectors útiles
  readonly hasRoute = computed(() => this.state().route.length > 0);
  readonly gasCount = computed(() => this.state().gasStations.length);
  readonly weatherCount = computed(() => this.state().weather?.length ?? 0);

  setLoading(loading: boolean) {
    this._state.update(s => ({ ...s, loading, error: null }));
  }

  setError(message: string) {
    this._state.update(s => ({ ...s, loading: false, error: message }));
  }

  setData(route: Coords[], gasStations: Coords[], weather: RouteWeatherPoint[] | null) {
    this._state.set({
      route: route ?? [],
      gasStations: gasStations ?? [],
      weather: weather ?? null,
      loading: false,
      error: null,
    });
  }

  clear() {
    this._state.set(initialState);
  }

  setRoute(route: Coords[]) {
  this._state.update(s => ({
    ...s,
    route: route ?? [],
    loading: false,
    error: null,
  }));
}
}
