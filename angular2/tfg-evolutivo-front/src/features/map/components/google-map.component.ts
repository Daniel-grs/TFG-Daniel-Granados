import { Component, effect, signal, ViewChild } from '@angular/core';
import { GoogleMapsModule, GoogleMap } from '@angular/google-maps';
import { MapStateService } from '../../../app/services/map-state.service';

@Component({
  selector: 'app-google-map',
  standalone: true,
  imports: [GoogleMapsModule],
  template: `
    <google-map
      #map
      height="100%"
      width="100%"
      [center]="center()"
      [zoom]="zoom()"
      [options]="mapOptions"
    >
      @if (path().length > 0) {
        <map-polyline [path]="path()" [options]="polylineOptions"></map-polyline>
      }

      @for (m of gasMarkers(); track $index) {
        <map-marker [position]="m"></map-marker>
      }
    </google-map>
  `,
  styles: [':host{display:block;height:100%;}']
})
export class GoogleMapComponent {
  @ViewChild('map', { static: false }) map?: GoogleMap;

  mapOptions: google.maps.MapOptions = {
    clickableIcons: false,
  };

  // Que se vea sí o sí
  polylineOptions: google.maps.PolylineOptions = {
    strokeOpacity: 1,
    strokeWeight: 6,
  };

  center = signal<google.maps.LatLngLiteral>({ lat: 40.4168, lng: -3.7038 });
  zoom = signal<number>(7);

  path = signal<google.maps.LatLngLiteral[]>([]);
  gasMarkers = signal<google.maps.LatLngLiteral[]>([]);

  constructor(private readonly state: MapStateService) {
    effect(() => {
      const s = this.state.state();
      console.log('STATE ROUTE SIZE', s.route.length);
      const route = s.route.map(c => ({ lat: c.lat, lng: c.lng }));
      const gas = s.gasStations.map(c => ({ lat: c.lat, lng: c.lng }));

      this.path.set(route);
      this.gasMarkers.set(gas);

      // Ajustar cámara a toda la ruta (cuando el mapa ya existe)
      if (route.length && this.map?.googleMap) {
        const bounds = new google.maps.LatLngBounds();
        route.forEach(p => bounds.extend(p));
        this.map.googleMap.fitBounds(bounds);
      }
    });
  }
}