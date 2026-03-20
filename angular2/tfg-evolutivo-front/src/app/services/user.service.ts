import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { environment } from '../../environments/environment';
import { UserBasicInfo } from '../models/user-basic-info';
import { UserPreferences } from '../models/user-preferences';
import { EnumOption } from '../models/enum-options';
import { SavedRoute } from '../models/saved-route';
import { UserSavedGasStation } from '../models/user-saved-gas-station';
import { PreferredBrandsRequest } from '../models/preferred-brands';
import { RouteExecution } from '../models/route-execution';
import { GasStation } from "../models/gas-station";

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  getCurrentUser() {
    return this.http.get<UserBasicInfo>(this.baseUrl + '/api/users/get', {
      withCredentials: true,
    });
  }

  getLanguages() {
    return this.http.get<EnumOption[]>(this.baseUrl + '/api/preferences/languages', {
      withCredentials: true,
    });
  }

  getThemes() {
    return this.http.get<EnumOption[]>(this.baseUrl + '/api/preferences/themes', {
      withCredentials: true,
    });
  }

  getUserPreferences() {
    return this.http.get<UserPreferences>(this.baseUrl + '/api/users/preferences/user/get', {
      withCredentials: true,
    });
  }

  updateUserPreferences(theme: string, language: string) {
    const params = new HttpParams()
      .set('theme', theme)
      .set('language', language);

    return this.http.put<void>(this.baseUrl + '/api/users/preferences/user/update', null, {
      params,
      withCredentials: true,
    });
  }

  getSavedRoutes() {
    return this.http.get<SavedRoute[]>(this.baseUrl + '/api/savedRoute', {
      withCredentials: true,
    });
  }

  deleteSavedRoute(routeId: number) {
    return this.http.delete<void>(this.baseUrl + `/api/savedRoute/delete/${routeId}`, {
      withCredentials: true,
    });
  }

  renameSavedRoute(routeId: number, newName: string) {
    const params = new HttpParams()
      .set('routeId', routeId)
      .set('newName', newName);

    return this.http.post<SavedRoute>(this.baseUrl + '/api/savedRoute/rename', null, {
      params,
      withCredentials: true,
    });
  }

  executeSavedRoute(routeId: number) {
    const params = new HttpParams().set('id', routeId);
    return this.http.get<RouteExecution>(this.baseUrl + '/api/savedRoute/execute', {
      params,
      withCredentials: true,
    });
  }

  getFavouriteStations() {
    return this.http.get<UserSavedGasStation[]>(this.baseUrl + '/api/users/favouriteStations', {
      withCredentials: true,
    });
  }

  saveFavouriteStation(alias: string, idEstacion: number) {
    const params = new HttpParams()
      .set('alias', alias)
      .set('idEstacion', idEstacion);

    return this.http.put<void>(this.baseUrl + '/api/users/favouriteStations', null, {
      params,
      withCredentials: true,
    });
  }

  renameFavouriteStation(oldAlias: string, newAlias: string) {
    const params = new HttpParams()
      .set('oldAlias', oldAlias)
      .set('newAlias', newAlias);

    return this.http.post<void>(this.baseUrl + '/api/users/favouriteStations', null, {
      params,
      withCredentials: true,
    });
  }

  deleteFavouriteStation(alias: string) {
    const params = new HttpParams().set('alias', alias);

    return this.http.delete<void>(this.baseUrl + '/api/users/favouriteStations', {
      params,
      withCredentials: true,
    });
  }

  getPreferredBrands() {
    return this.http.get<string[]>(this.baseUrl + '/api/users/preferredBrands/get', {
      withCredentials: true,
    });
  }

  updateRoutePreferences(
    body: PreferredBrandsRequest,
    radioKm: number,
    fuelType: string,
    maxPrice: number,
    mapView: string,
    avoidTolls: boolean,
    vehicleEmissionType: string,
  ) {
    let params = new HttpParams()
      .set('radioKm', radioKm)
      .set('fuelType', fuelType)
      .set('maxPrice', maxPrice)
      .set('mapView', mapView)
      .set('avoidTolls', avoidTolls)
      .set('vehicleEmissionType', vehicleEmissionType);

    return this.http.put<void>(this.baseUrl + '/api/users/preferences/update', body, {
      params,
      withCredentials: true,
    });
  }

  searchGasStations(lat: number, lng: number, radius: number) {
  return this.http.get<GasStation[]>(
    this.baseUrl + '/api/oil/gasolineras/radio/coords',
    {
      params: {
        latitud: lat,
        longitud: lng,
        radius: radius,
      },
      withCredentials: true,
    }
  );
}
}