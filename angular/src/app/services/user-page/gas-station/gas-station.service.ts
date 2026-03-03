import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GasStation } from '../../../Dto/gas-station';
import { Municipalitie } from '../../../Dto/municipalities';

@Injectable({
  providedIn: 'root'
})
export class GasStationService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  searchGasStations(address: string, radio: number): Observable<GasStation[]> {
    const formattedAddress = address.replace(/\s+/g, '_');
    const url = `${this.baseUrl}/api/oil/gasolineras/radio/address?direccion=${encodeURIComponent(formattedAddress)}&radio=${radio}`;
    return this.http.get<GasStation[]>(url, { withCredentials: true });
  }

  getGasStation(idEstacion: number): Observable<GasStation> {
    return this.http.get<GasStation>(`${this.baseUrl}/api/oil/id/${idEstacion}`, { withCredentials: true });
  }

  getGasStationFromDirectionInRadius(direccion: string, radio: number): Observable<GasStation[]> {
    return this.http.get<GasStation[]>(`${this.baseUrl}/api/oil/gasolineras/radio/address`, {
      params: { direccion, radio }, withCredentials: true
    })
  }

  getGasStationBrands(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/api/oil/gasolineras/marcas`, { withCredentials: true });
  }

  getMunicipalities(): Observable<Municipalitie[]> {
    return this.http.get<Municipalitie[]>(`${this.baseUrl}/api/oil/municipios`, { withCredentials: true });
  }
}