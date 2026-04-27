import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { RouteRequest } from '../models/route-request';
import { Coords } from '../models/coords';
import { CoordsWithWeather } from '../models/coords-with-weather';
import { RouteWeatherPoint } from '../models/route-weather';
import { RouteSummary } from '../models/route-summary';

@Injectable({ providedIn: 'root' })
export class RouteApiService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  private buildParams(req: RouteRequest): HttpParams {
    let params = new HttpParams()
      .set('origin', req.origin.trim())
      .set('destination', req.destination.trim());

    // waypoints como lista repetida: &waypoints=A&waypoints=B
    (req.waypoints ?? [])
      .map(w => w.trim())
      .filter(Boolean)
      .forEach(w => params = params.append('waypoints', w));

    if (req.optimizeWaypoints !== undefined) params = params.set('optimizeWaypoints', String(!!req.optimizeWaypoints));
    if (req.optimizeRoute !== undefined) params = params.set('optimizeRoute', String(!!req.optimizeRoute));
    if (req.language) params = params.set('language', req.language);
    if (req.avoidTolls !== undefined) params = params.set('avoidTolls', String(!!req.avoidTolls));

    return params;
  }

  private parseJson<T>(raw: string): T {
    try {
      return JSON.parse(raw) as T;
    } catch {
      throw new Error('El backend devolvió texto que no es JSON válido');
    }
  }

  private toNumber(v: any): number | null {
  const n = typeof v === 'string' ? Number(v) : v;
  return Number.isFinite(n) ? n : null;
}

private normalizeCoord(p: any): { lat: number; lng: number } | null {
  const lat = this.toNumber(p?.lat ?? p?.latitude ?? p?.latitud);
  const lng = this.toNumber(p?.lng ?? p?.lon ?? p?.longitude ?? p?.longitud);
  if (lat === null || lng === null) return null;
  return { lat, lng };
}

private normalizeCoordsArray(raw: any): { lat: number; lng: number }[] {
  if (!Array.isArray(raw)) return [];
  return raw.map(p => this.normalizeCoord(p)).filter((x): x is {lat:number;lng:number} => !!x);
}

  getPolylineCoords(req: RouteRequest): Observable<Coords[]> {
  return this.http.get(this.baseUrl + '/api/route/polylineCords', {
    params: this.buildParams(req),
    responseType: 'text',
  }).pipe(
    map(raw => this.parseJson<any>(raw)),
    map(rawArr => this.normalizeCoordsArray(rawArr))
  );
}

getGasStationsCoords(req: RouteRequest): Observable<Coords[]> {
  const radius = req.radius ?? 2;
  const params = this.buildParams(req).set('radius', String(radius));

  return this.http.get(this.baseUrl + '/api/routes/gasStations', {
    params,
    responseType: 'text',
  }).pipe(
    map(raw => this.parseJson<any>(raw)),
    map(rawArr => this.normalizeCoordsArray(rawArr))
  );
}

  getRouteWeather(req: RouteRequest) {
  return this.http.get(this.baseUrl + '/api/routes/weather', {
    params: this.buildParams(req),
    responseType: 'text',
  }).pipe(
    map(raw => this.parseJson<RouteWeatherPoint[]>(raw))
  );
}
saveRoute(alias: string, req: RouteRequest) {
  const params = this.buildParams(req).set('name', alias.trim());

  return this.http.post(this.baseUrl + '/api/savedRoute/save', null, {
    params,
    withCredentials: true,
    responseType: 'text',
  });
}
getRouteSummary(req: RouteRequest) {
  return this.http.get(this.baseUrl + '/api/routes', {
    params: this.buildParams(req),
    responseType: 'text',
  }).pipe(
    map(raw => this.parseJson<any>(raw)),
    map((raw): RouteSummary => {
      const firstRoute = raw?.routes?.[0];

      const distanceMeters =
        firstRoute?.legs?.reduce((acc: number, leg: any) => acc + (leg?.distance?.value ?? 0), 0) ?? null;

      const durationSeconds =
        firstRoute?.legs?.reduce((acc: number, leg: any) => acc + (leg?.duration?.value ?? 0), 0) ?? null;

      return {
        distanceMeters,
        durationSeconds,
      };
    })
  );
}
}