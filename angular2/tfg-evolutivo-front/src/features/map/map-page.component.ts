import { Component } from '@angular/core';
import { RouteFormComponent } from './components/route-form.component';
import { WeatherWidgetComponent } from './components/weather-widget.component';
import { GoogleMapComponent } from './components/google-map.component';
import { MapStateService } from '../../app/services/map-state.service';

@Component({
  selector: 'app-map-page',
  standalone: true,
  imports: [RouteFormComponent, WeatherWidgetComponent, GoogleMapComponent],
  template: `
  <div class="map-page page-shell">
    <section class="top-panel card">
      <app-route-form></app-route-form>
    </section>

    @if (mapState.hasRoute()) {
      <section class="route-summary card">
        <div class="summary-item">
          <span class="label">Distancia</span>
          <strong>{{ mapState.routeDistanceKm() ? mapState.routeDistanceKm() + ' km' : 'N/D' }}</strong>
        </div>

        <div class="summary-item">
          <span class="label">Duración</span>
          <strong>{{ mapState.routeDurationText() ?? 'N/D' }}</strong>
        </div>

        <div class="summary-item">
          <span class="label">Gasolineras</span>
          <strong>{{ mapState.gasCount() }}</strong>
        </div>


      </section>
    }

    <section class="map-section">
      <div class="map-card card">
        <app-google-map></app-google-map>
      </div>

      <aside class="weather-panel card">
        <app-weather-widget></app-weather-widget>
      </aside>
    </section>
  </div>
`,
  styles: [`
    .map-page{
      padding: 20px 0 24px;
      display: grid;
      gap: 16px;
    }

    .top-panel{
      padding: 18px;
      background: var(--bg-elevated);
      border: 1px solid var(--border);
    }

    .map-section{
      display: grid;
      grid-template-columns: 1fr 320px;
      gap: 16px;
      align-items: start;
    }

    .map-card{
      overflow: hidden;
      min-height: calc(100vh - 270px);
      border: 1px solid var(--border);
    }

    .map-card app-google-map{
      display: block;
      width: 100%;
      height: calc(100vh - 270px);
      min-height: 520px;
    }

    .weather-panel{
      padding: 14px;
      min-height: 220px;
      background: var(--bg-elevated);
      border: 1px solid var(--border);
    }

    @media (max-width: 1100px){
      .map-section{
        grid-template-columns: 1fr;
      }

      .weather-panel{
        order: -1;
      }
    }
      .route-summary{
  display:grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap:12px;
  padding:16px;
}

.summary-item{
  display:grid;
  gap:4px;
  padding:14px;
  border-radius:14px;
  background: var(--input-bg);
  border: 1px solid var(--border);
}

.summary-item .label{
  font-size:13px;
  color: var(--text-soft);
}

.summary-item strong{
  font-size:20px;
  color: var(--text);
}

@media (max-width: 900px){
  .route-summary{
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
  `]
})
export class MapPageComponent {
  constructor(public readonly mapState: MapStateService) {}
}