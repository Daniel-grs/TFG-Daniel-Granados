import { Component, computed, inject } from '@angular/core';
import { MapStateService } from '../../../app/services/map-state.service';

@Component({
  selector: 'app-weather-widget',
  standalone: true,
  template: `
    <div class="weather-widget">
      <h3 class="weather-widget__title">Tiempo en ruta</h3>

      @if (state().loading) {
        <p class="weather-widget__muted">Cargando...</p>
      }

      @if (state().error) {
        <p class="weather-widget__error">{{ state().error }}</p>
      }

      @if (!points().length && !state().loading) {
        <p class="weather-widget__muted">Sin datos todavía.</p>
      }

      <div class="weather-widget__list">
        @for (point of points(); track point.address) {
          <article class="weather-widget__item">
            <div class="weather-widget__address">
              {{ point.address }}
            </div>

            <div class="weather-widget__meta">
              <span>
                Hora estimada:
                <strong>
                  {{
                    point.estimatedArrivalHour !== null
                      ? (point.estimatedArrivalHour + ':00')
                      : 'N/D'
                  }}
                </strong>
              </span>

              <span>
                Temperatura:
                <strong>
                  {{
                    point.temperature !== null
                      ? (point.temperature + ' ºC')
                      : 'N/D'
                  }}
                </strong>
              </span>
            </div>

            <div class="weather-widget__desc">
              {{ point.weatherDescription || 'Sin descripción disponible' }}
            </div>
          </article>
        }
      </div>
    </div>
  `,
  styles: [`
    .weather-widget{
      display:grid;
      gap:12px;
    }

    .weather-widget__title{
      margin:0;
      font-size:1.2rem;
      font-weight:700;
      color:var(--text);
    }

    .weather-widget__list{
      display:grid;
      gap:10px;
    }

    .weather-widget__item{
      padding:12px;
      border:1px solid var(--border);
      border-radius:14px;
      background: var(--input-bg);
      display:grid;
      gap:8px;
    }

    .weather-widget__address{
      font-weight:600;
      color:var(--text);
      line-height:1.35;
    }

    .weather-widget__meta{
      display:flex;
      flex-direction:column;
      gap:4px;
      color:var(--text-soft);
      font-size:14px;
    }

    .weather-widget__desc{
      color:var(--text);
      font-size:14px;
      line-height:1.35;
    }

    .weather-widget__muted{
      margin:0;
      color:var(--text-soft);
    }

    .weather-widget__error{
      margin:0;
      color:#d44;
    }
  `]
})
export class WeatherWidgetComponent {
  private readonly mapState = inject(MapStateService);
  state = this.mapState.state;

  points = computed(() => this.state().weather ?? []);
}