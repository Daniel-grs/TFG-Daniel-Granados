import { Component } from '@angular/core';
import { RouteFormComponent } from './components/route-form.component';
import { WeatherWidgetComponent } from './components/weather-widget.component';
import { GoogleMapComponent } from '../map/components/google-map.component';

@Component({
  selector: 'app-map-page',
  standalone: true,
  imports: [RouteFormComponent, WeatherWidgetComponent, GoogleMapComponent],
  template: `
    <div class="layout">
      <aside class="panel">
        <h2 style="margin-top:0;">Ruta</h2>
        <app-route-form></app-route-form>

        <div style="height:12px;"></div>
        <app-weather-widget></app-weather-widget>
      </aside>

      <main class="map">
        <app-google-map></app-google-map>
      </main>
    </div>
  `,
  styles: [`
    .layout{
      display:grid;
      grid-template-columns: 380px 1fr;
      gap: 16px;
      height: calc(100vh - 24px);
      padding: 12px;
    }
    .panel{
      border:1px solid #ddd;
      border-radius: 12px;
      padding: 12px;
      overflow:auto;
    }
    .map{
      border:1px solid #ddd;
      border-radius: 12px;
      overflow:hidden;
      min-height: 400px;
    }
    input, select, button{
      width:100%;
      padding: 8px;
      box-sizing: border-box;
    }
    button{ cursor:pointer; }
  `]
})
export class MapPageComponent {}