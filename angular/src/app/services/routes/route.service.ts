// src/app/services/routes/route.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { Coords } from '../../Dto/maps-dtos';
import { WeatherData } from '../../Dto/weather-dtos';
import { GasStation } from '../../Dto/gas-station';

@Injectable({ providedIn: 'root' })
export class RouteService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  /** Backend espera un header 'key' (según tu código actual). */
  private headers(): HttpHeaders {
    return new HttpHeaders().set('key', environment.googleMapsMapId);
  }

  private toParams(form: RouteFormResponse): HttpParams {
    const waypointsString = (form.waypoints ?? []).filter(Boolean).join('|');

    let params = new HttpParams()
      .set('origin', form.origin ?? '')
      .set('destination', form.destination ?? '')
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', String(!!form.optimizeWaypoints))
      .set('optimizeRoute', String(!!form.optimizeRoute));

    if (form.avoidTolls !== undefined) params = params.set('avoidTolls', String(!!form.avoidTolls));
    if (form.vehiculeEmissionType) params = params.set('vehiculeEmissionType', form.vehiculeEmissionType);

    return params;
  }

  private parseJson<T>(raw: string): T {
    try {
      return JSON.parse(raw) as T;
    } catch {
      throw new Error('Respuesta del backend no es JSON válido');
    }
  }

  /** Polyline como lista de coords */
  getPolylineCoords(form: RouteFormResponse): Observable<Coords[]> {
    return this.http
      .get(this.baseUrl + '/api/route/polylineCords', {
        headers: this.headers(),
        params: this.toParams(form),
        responseType: 'text',
      })
      .pipe(map((raw) => this.parseJson<Coords[]>(raw)));
  }

  /** Puntos/legs coords */
  getLegCoords(form: RouteFormResponse): Observable<Coords[]> {
    return this.http
      .get(this.baseUrl + '/api/route/legCoords', {
        headers: this.headers(),
        params: this.toParams(form),
        responseType: 'text',
      })
      .pipe(map((raw) => this.parseJson<Coords[]>(raw)));
  }

  /** Coordenadas para buscar gasolineras en torno a la ruta */
  getGasStationsCoords(form: RouteFormResponse): Observable<Coords[]> {
    const params = this.toParams(form).set('radius', String(form.radioKm ?? 2));

    return this.http
      .get(this.baseUrl + '/api/routes/gasStations', {
        headers: this.headers(),
        params,
        responseType: 'text',
      })
      .pipe(map((raw) => this.parseJson<Coords[]>(raw)));
  }

  /** Tiempo a lo largo de la ruta */
  getWeatherRoute(form: RouteFormResponse): Observable<WeatherData[]> {
    return this.http
      .get(this.baseUrl + '/api/routes/weather', {
        headers: this.headers(),
        params: this.toParams(form),
        responseType: 'text',
      })
      .pipe(map((raw) => this.parseJson<WeatherData[]>(raw)));
  }

  /** Tus gasolineras por radio */
  getGasStationsByCoords(lat: number, lng: number, radio = 1): Observable<GasStation[]> {
    return this.http.get<GasStation[]>(this.baseUrl + '/api/oil/gasolineras/radio/coords', {
      params: { latitud: lat, longitud: lng, radio },
      withCredentials: true,
    });
  }

  saveFavouriteRoute(alias: string, form: RouteFormResponse) {
    const params = this.toParams(form).set('name', alias);

    return this.http.post(this.baseUrl + '/api/savedRoute/save', null, {
      params,
      withCredentials: true,
    });
  }
}