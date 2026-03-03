import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { Coords, RouteGroupResponse } from '../../Dto/maps-dtos';
import { GasStation } from '../../Dto/gas-station';


@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = 'http://localhost:8080';
  private routeUrl = 'http://localhost:8080/api/savedRoute'
  constructor(private http: HttpClient) { }

  calculateRoute(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)


    return this.http.get(this.apiUrl, { headers: headers, params: parameters, responseType: 'text' });
  }


  calculatePolylineCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('avoidTolls', routeFormResponse.avoidTolls)
      .set('vehiculeEmissionType', routeFormResponse.vehiculeEmissionType)
    return this.http.get(this.apiUrl + "/api/route/polylineCords", { headers: headers, params: parameters, responseType: 'text' });
  }

  calculatePointCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('avoidTolls', routeFormResponse.avoidTolls)
      .set('vehiculeEmissionType', routeFormResponse.vehiculeEmissionType)

    return this.http.get(this.apiUrl + "/api/route/legCoords", { headers: headers, params: parameters, responseType: 'text' });
  }

  calculateGasStations(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('radius', routeFormResponse.radioKm || 2)

    return this.http.get(this.apiUrl + "/api/routes/gasStations", { headers: headers, params: parameters, responseType: 'text' });
  }

    getGasStationsByCoords(lat: number, lng: number, radio: number = 1): Observable<GasStation[]> {
    return this.http.get<GasStation[]>(`${this.apiUrl}/api/oil/gasolineras/radio/coords`, {
      params: { latitud: lat, longitud: lng, radio: radio },
      withCredentials: true
    });
  }

  calculateWeatherRoute(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);
    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('avoidTolls', routeFormResponse.avoidTolls)
      .set('vehiculeEmissionType', routeFormResponse.vehiculeEmissionType)

    return this.http.get(this.apiUrl + "/api/routes/weather", { headers: headers, params: parameters, responseType: 'text' });
  }

  saveFavouriteRoute(alias: string, routeFormResponse: RouteFormResponse) {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    let waypointsString = routeFormResponse.waypoints.join('|');
    let params = new HttpParams()
      .set('name', alias)
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)

    console.log(params)
    return this.http.post(
    this.routeUrl + '/save',
    null,
    { params, withCredentials: true }
  );
  }

}
