import { Component, OnDestroy, signal, input, effect, AfterViewInit, ChangeDetectionStrategy } from '@angular/core';
import { setOptions, importLibrary } from '@googlemaps/js-api-loader';
import { MarkerClusterer } from '@googlemaps/markerclusterer';

import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { environment } from '../../../../environments/environment';
import { Coords } from '../../../Dto/maps-dtos';
import { CommonModule } from '@angular/common';
import { WeatherOverlayHostComponent } from './weather-overlay/weather-overlay-host/weather-overlay-host.component';
import { WeatherData } from '../../../Dto/weather-dtos';
import { GasStation } from '../../../Dto/gas-station';
import { GasStationSelectionService } from '../../../services/user-page/gas-station-selection/gas-station-selection.service';
import { TranslationService } from '../../../services/translation.service';
import { UserInfoService } from '../../../services/user-page/user-info.service';
import { UserPreferencesService } from '../../../services/user-page/user-preferences.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-map-page',
  standalone: true,
  imports: [CommonModule, WeatherOverlayHostComponent],
  templateUrl: './map-page.component.html',
  styleUrl: './map-page.component.css'
})
export class MapPageComponent implements OnDestroy, AfterViewInit {
  private static mapsOptionsSet = false;

  public map?: google.maps.Map;
  private routePolyline?: google.maps.Polyline;
  private startMarker?: any;
  private endMarker?: any;
  private waypoints: any[] = [];
  private gasStationsMarkers: any[] = [];
  private markerClusterer?: MarkerClusterer;
  protected weatherRoute = signal<WeatherData[] | null>(null);
  protected createdRoute = signal<boolean>(false);
  private previousSelected = signal<string | null>(null);

  protected selectedGasStation = signal<GasStation | null>(null);

  showWeather = input<boolean>(true);
  showControls = input<boolean>(true);
  gasStations = input<GasStation[]>([]);
  selectedStation = input<string | null>(null);
  mapType = input<string>('MAP');
  fitToStations = input<boolean>(true);

  constructor(
    private mapComm: MapCommunicationService,
    private gasStationSelectionService: GasStationSelectionService,
    protected translation: TranslationService,
    private userInfo: UserInfoService,
    private userPreferences: UserPreferencesService
  ) {
    effect(() => {
      this.updateMarkers();
    });

    effect(() => {
      this.remarkGasStation();
    });
  }

  async ngAfterViewInit(): Promise<void> {
    await this.initMap();
    this.checkUserPreferences();
  }

  ngOnDestroy(): void {
    try { this.mapComm.unregisterMapPage(this); } catch { }
    this.clearRoute();
  }

  private async initMap(): Promise<void> {
    if (!MapPageComponent.mapsOptionsSet) {
      setOptions({
        key: environment.googleMapsApiKey,
        v: 'weekly',
        language: this.translation.getCurrentLang().toLowerCase()
      });
      MapPageComponent.mapsOptionsSet = true;
    }

    const mapsLib = (await importLibrary('maps')) as unknown as any;
    (await importLibrary('marker')) as unknown as any;

    const { Map } = mapsLib;

    const mapOptions: google.maps.MapOptions = {
      center: { lat: 40.4168, lng: -3.7038 },
      zoom: 6,
      tilt: 45,
      mapTypeControl: this.showControls(),
      fullscreenControl: this.showControls(),
      streetViewControl: false,
      mapId: environment.googleMapsMapId,
      mapTypeId: this.getMapTypeId()
    };

    this.map = new Map(document.getElementById('map') as HTMLElement, mapOptions);
    this.markerClusterer = new MarkerClusterer({
      map: this.map,
      minimumClusterSize: 3
    } as any);
    this.mapComm.registerMapPage(this);

    this.updateMarkers();
  }

  private getMapTypeId(): google.maps.MapTypeId {
    const type = this.mapType();
    return this.getMapTypeIdFromRaw(type);
  }

  private getMapTypeIdFromRaw(type: string): google.maps.MapTypeId {
    switch (type) {
      case 'MAP': return google.maps.MapTypeId.ROADMAP;
      case 'MAP_RELIEF': return google.maps.MapTypeId.TERRAIN;
      case 'SATELLITE': return google.maps.MapTypeId.SATELLITE;
      case 'SATELLITE_LABELS': return google.maps.MapTypeId.HYBRID;
      default: return google.maps.MapTypeId.ROADMAP;
    }
  }

  private checkUserPreferences(): void {
    this.userInfo.isLoggedIn().subscribe(logged => {
      if (logged) {
        this.userPreferences.getUserPreferences().subscribe(pref => {
          const mapView = pref.mapView;
          if (this.map) {
            this.map.setMapTypeId(this.getMapTypeIdFromRaw(mapView));
          }
        });
      }
    });
  }

  public drawRoute(coords: Coords[]): void {
    if (!this.map) {
      console.warn('Map not initialized');
      return;
    }

    this.createdRoute.set(true);
    const path: google.maps.LatLngLiteral[] = coords;

    if (!this.routePolyline) {
      this.routePolyline = new google.maps.Polyline({
        path,
        geodesic: true,
        strokeColor: '#4285F4',
        strokeOpacity: 0.85,
        strokeWeight: 5,
        map: this.map
      });
    } else {
      this.routePolyline.setPath(path);
    }

    const bounds = new google.maps.LatLngBounds();
    path.forEach(p => bounds.extend(p));
    this.map.fitBounds(bounds, { top: 50, right: 50, bottom: 50, left: 50 });
  }

  public clearRoute(): void {
    if (this.routePolyline) {
      this.routePolyline.setMap(null);
      this.routePolyline = undefined;
    }

    if (this.waypoints && this.waypoints.length) {
      this.waypoints.forEach(wp => {
        wp.map = null;
      });
      this.waypoints = [];
    }

    this.clearGasStations();

    if (this.weatherRoute()) {
      this.weatherRoute.update(() => null);
    }

    if (this.startMarker) {
      this.startMarker.map = null;
      this.startMarker = undefined;
    }
    if (this.endMarker) {
      this.endMarker.map = null;
      this.endMarker = undefined;
    }

    this.createdRoute.set(false);
  }

  public drawPoints(coords: Coords[]): void {
    if (!this.map) {
      console.warn('Map not initialized');
      return;
    }
    if (!coords || coords.length === 0) return;

    coords.forEach((c, idx) => {
      const isStart = idx === 0;
      const isEnd = idx === coords.length - 1;

      const color = isStart || isEnd ? '#200f9dff' : '#4285F4';
      const size = 18;
      const stroke = Math.max(1, Math.round(size * 0.12));
      const cx = size / 2;
      const cy = size / 2;
      const r = Math.max(1, Math.round((size / 2) - stroke - 1));

      const circleSvg = `
      <svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" xmlns="http://www.w3.org/2000/svg">
        <circle cx="${cx}" cy="${cy}" r="${r}" fill="${color}" stroke="#ffffff" stroke-width="${stroke}"/>
      </svg>
    `;

      const parsed = new DOMParser().parseFromString(circleSvg, 'image/svg+xml').documentElement;
      parsed.style.position = 'absolute';
      parsed.style.left = '0';
      parsed.style.top = '0';
      parsed.style.transform = 'translate(-50%, -50%)';
      parsed.style.transformOrigin = 'center center';
      const advancedMarker = new google.maps.marker.AdvancedMarkerElement({
        map: this.map,
        position: { lat: c.lat, lng: c.lng },
        content: parsed,
      });
      this.waypoints.push(advancedMarker);

      if (isStart) this.startMarker = advancedMarker;
      if (isEnd) this.endMarker = advancedMarker;
    });
  }

  public markGasStations(gasStationsCoords: Coords[]): void {
    if (!this.map) {
      console.warn('Map not initialized');
      return;
    }

    if (!gasStationsCoords || gasStationsCoords.length === 0) return;

    const size = 36;
    const gasStationPointSvg = `
      <svg height="${size}" width="${size}" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
      viewBox="0 0 624.138 624.138" xml:space="preserve" fill="#e71616ff"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <g> <g> <path style="fill:#e71616ff;" d="M312.064,0C203.478,0,115.439,88.029,115.439,196.566c0,108.634,196.625,427.572,196.625,427.572 S508.698,305.2,508.698,196.566C508.698,88.029,420.698,0,312.064,0z M312.074,348.678 c-83.701,0-151.809-68.108-151.809-151.809c0-83.721,68.118-151.809,151.809-151.809c83.701,0,151.809,68.088,151.809,151.809 C463.883,280.57,395.775,348.678,312.074,348.678z"></path> <path style="fill:#010002;" d="M372.658,122.41l20.078,20.498v106.006c0,1.729-0.352,2.97-1.094,3.703 c-1.7,1.671-5.921,1.563-8.109,1.544c-4.719,0-8.275-0.938-9.956-2.609c-1.075-1.133-1.133-2.276-1.075-2.638V145.888H354.74 V95.2h-98.806v182.409H234.42v25.344h141.882v-25.344H354.74V156.01h7.591v92.475c-0.039,0.547-0.274,5.452,3.664,9.78 c3.722,3.996,9.565,6.028,17.42,6.028l1.514,0.02c3.742,0,9.682-0.449,13.805-4.484c2.745-2.697,4.133-6.36,4.133-10.933 V138.746l-22.989-23.448L372.658,122.41z M342.039,166.151h-70.922v-50.668h70.922V166.151z"></path> </g> </g> </g> </g> </g>
      </svg>
    `;

    gasStationsCoords.forEach(c => {
      const container = document.createElement('div');
      container.innerHTML = gasStationPointSvg;
      container.style.width = '36px';
      container.style.height = '36px';
      container.style.display = 'block';
      container.style.transform = 'translateY(-6px)';

      const marker = new google.maps.marker.AdvancedMarkerElement({
        map: null,
        position: { lat: c.lat, lng: c.lng },
        content: container,
        title: (c as any).name || 'Gasolinera'
      });
      this.gasStationsMarkers.push(marker);
      this.markerClusterer?.addMarker(marker);
    });
  }

  private updateMarkers(): void {
    const stations = this.gasStations();
    this.clearGasStations();

    if (stations.length > 100) {
      console.warn('Limiting gas stations to 100 to prevent performance issues');
      stations.splice(100);
    }

    if (stations.length > 0 && this.map) {
      let hasValid = false;
      const bounds = new google.maps.LatLngBounds();
      const positionCount = new Map<string, number>();

      stations.forEach(station => {
        const lat = Number(station.latitud);
        const lng = Number(station.longitud);
        if (isNaN(lat) || isNaN(lng) || lat < -90 || lat > 90 || lng < -180 || lng > 180) {
          console.warn('Invalid coords for station:', station.nombreEstacion, 'lat:', station.latitud, 'lng:', station.longitud);
          return;
        }

        const key = `${lat},${lng}`;
        const count = positionCount.get(key) || 0;
        positionCount.set(key, count + 1);

        const offsetLat = count * 0.0001;
        const offsetLng = count * 0.0001;
        const adjustedLat = lat + offsetLat;
        const adjustedLng = lng + offsetLng;

        const size = 36;
        const fillColor = '#e71616';
        const gasStationPointSvg = `
        <svg height="${size}" width="${size}" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
        viewBox="0 0 624.138 624.138" xml:space="preserve" fill="${fillColor}"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <g> <g> <path style="fill:${fillColor};" d="M312.064,0C203.478,0,115.439,88.029,115.439,196.566c0,108.634,196.625,427.572,196.625,427.572 S508.698,305.2,508.698,196.566C508.698,88.029,420.698,0,312.064,0z M312.064,348.678 c-83.701,0-151.809-68.108-151.809-151.809c0-83.721,68.118-151.809,151.809-151.809c83.701,0,151.809,68.088,151.809,151.809 C463.873,280.57,395.765,348.678,312.064,348.678z"></path> <path style="fill:#010002;" d="M372.658,122.41l20.078,20.498v106.006c0,1.729-0.352,2.97-1.094,3.703 c-1.7,1.671-5.921,1.563-8.109,1.544c-4.719,0-8.275-0.938-9.956-2.609c-1.075-1.133-1.133-2.276-1.075-2.638V145.888H354.74 V95.2h-98.806v182.409H234.42v25.344h141.882v-25.344H354.74V156.01h7.591v92.475c-0.039,0.547-0.274,5.452,3.664,9.78 c3.722,3.996,9.565,6.028,17.42,6.028l1.514,0.02c3.742,0,9.682-0.449,13.805-4.484c2.745-2.697,4.133-6.36,4.133-10.933 V138.746l-22.989-23.448L372.658,122.41z M342.039,166.151h-70.922v-50.668h70.922V166.151z"></path> </g> </g> </g> </g> </g>
        </svg>
        `;

        const container = document.createElement('div');
        container.innerHTML = gasStationPointSvg;
        container.style.width = `${size}px`;
        container.style.height = `${size}px`;
        container.style.display = 'block';
        container.style.transform = 'translateY(-6px)';
        container.style.cursor = 'pointer';

        const marker = new google.maps.marker.AdvancedMarkerElement({
          map: null,
          position: { lat: adjustedLat, lng: adjustedLng },
          content: container,
          title: `${station.nombreEstacion} - ${station.direccion}`
        });

        marker.addListener('click', () => {
          this.selectedGasStation.set(station);
          this.gasStationSelectionService.selectedStation.set(`${station.nombreEstacion} - ${station.direccion}`);
        });
        this.gasStationsMarkers.push(marker);
        this.markerClusterer?.addMarker(marker);
        bounds.extend({ lat: adjustedLat, lng: adjustedLng });
        hasValid = true;
      });

      if (hasValid) {
        if (this.fitToStations()) {
          this.map.fitBounds(bounds, { top: 70, right: 120, bottom: 150, left: 120 });
        }
      } else {
        console.log('No valid gas stations to display on map');
      }
    }
  }

  private remarkGasStation(): void {
    const currentSelected = this.selectedStation();
    const prevSelected = this.previousSelected();

    if (prevSelected && prevSelected !== currentSelected) {
      const prevMarker = this.gasStationsMarkers.find(m => m.title === prevSelected);
      if (prevMarker) {
        const size = 36;
        const fillColor = '#e71616';
        const gasStationPointSvg = `
          <svg height="${size}" width="${size}" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
          viewBox="0 0 624.138 624.138" xml:space="preserve" fill="${fillColor}"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <g> <g> <path style="fill:${fillColor};" d="M312.064,0C203.478,0,115.439,88.029,115.439,196.566c0,108.634,196.625,427.572,196.625,427.572 S508.698,305.2,508.698,196.566C508.698,88.029,420.698,0,312.064,0z M312.064,348.678 c-83.701,0-151.809-68.108-151.809-151.809c0-83.721,68.118-151.809,151.809-151.809c83.701,0,151.809,68.088,151.809,151.809 C463.873,280.57,395.765,348.678,312.064,348.678z"></path> <path style="fill:#010002;" d="M372.658,122.41l20.078,20.498v106.006c0,1.729-0.352,2.97-1.094,3.703 c-1.7,1.671-5.921,1.563-8.109,1.544c-4.719,0-8.275-0.938-9.956-2.609c-1.075-1.133-1.133-2.276-1.075-2.638V145.888H354.74 V95.2h-98.806v182.409H234.42v25.344h141.882v-25.344H354.74V156.01h7.591v92.475c-0.039,0.547-0.274,5.452,3.664,9.78 c3.722,3.996,9.565,6.028,17.42,6.028l1.514,0.02c3.742,0,9.682-0.449,13.805-4.484c2.745-2.697,4.133-6.36,4.133-10.933 V138.746l-22.989-23.448L372.658,122.41z M342.039,166.151h-70.922v-50.668h70.922V166.151z"></path> </g> </g> </g> </g> </g>
          </svg>
        `;
        const container = document.createElement('div');
        container.innerHTML = gasStationPointSvg;
        container.style.width = `${size}px`;
        container.style.height = `${size}px`;
        container.style.display = 'block';
        container.style.transform = 'translateY(-6px)';
        prevMarker.content = container;
      }
    }

    if (currentSelected) {
      const currentMarker = this.gasStationsMarkers.find(m => m.title === currentSelected);
      if (currentMarker) {
        const size = 48;
        const fillColor = '#940202ff';
        const gasStationPointSvg = `
          <svg height="${size}" width="${size}" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
          viewBox="0 0 624.138 624.138" xml:space="preserve" fill="${fillColor}"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <g> <g> <path style="fill:${fillColor};" d="M312.064,0C203.478,0,115.439,88.029,115.439,196.566c0,108.634,196.625,427.572,196.625,427.572 S508.698,305.2,508.698,196.566C508.698,88.029,420.698,0,312.064,0z M312.064,348.678 c-83.701,0-151.809-68.108-151.809-151.809c0-83.721,68.118-151.809,151.809-151.809c83.701,0,151.809,68.088,151.809,151.809 C463.873,280.57,395.765,348.678,312.064,348.678z"></path> <path style="fill:#010002;" d="M372.658,122.41l20.078,20.498v106.006c0,1.729-0.352,2.97-1.094,3.703 c-1.7,1.671-5.921,1.563-8.109,1.544c-4.719,0-8.275-0.938-9.956-2.609c-1.075-1.133-1.133-2.276-1.075-2.638V145.888H354.74 V95.2h-98.806v182.409H234.42v25.344h141.882v-25.344H354.74V156.01h7.591v92.475c-0.039,0.547-0.274,5.452,3.664,9.78 c3.722,3.996,9.565,6.028,17.42,6.028l1.514,0.02c3.742,0,9.682-0.449,13.805-4.484c2.745-2.697,4.133-6.36,4.133-10.933 V138.746l-22.989-23.448L372.658,122.41z M342.039,166.151h-70.922v-50.668h70.922V166.151z"></path> </g> </g> </g> </g> </g>
          </svg>
        `;
        const container = document.createElement('div');
        container.innerHTML = gasStationPointSvg;
        container.style.width = `${size}px`;
        container.style.height = `${size}px`;
        container.style.display = 'block';
        container.style.transform = 'translateY(-6px)';
        currentMarker.content = container;
      }

      const selectedStationData = this.gasStations().find(s => `${s.nombreEstacion} - ${s.direccion}` === currentSelected);
      if (selectedStationData && this.map) {
        this.map.setCenter({ lat: selectedStationData.latitud, lng: selectedStationData.longitud });
        this.map.setZoom(15);
      }
    }

    this.previousSelected.set(currentSelected);
  }

  public closeWidget(): void {
    this.selectedGasStation.set(null);
  }

  public getStationType(tipoVenta: string): string {
    return tipoVenta;
  }

  private clearGasStations(): void {
    if (this.gasStationsMarkers && this.gasStationsMarkers.length) {
      this.gasStationsMarkers.forEach(marker => {
        this.markerClusterer?.removeMarker(marker);
        marker.map = null;
      });
      this.gasStationsMarkers = [];
    }
  }

  public setWeatherData(data: WeatherData[] | null): void {
    this.weatherRoute.update(() => data);
  }
}