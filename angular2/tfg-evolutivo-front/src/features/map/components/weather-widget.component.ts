import { Component, computed, inject } from '@angular/core';
import { MapStateService } from '../../../app/services/map-state.service';
import { RouteWeatherPoint } from '../../../app/models/route-weather';

@Component({
  selector: 'app-weather-widget',
  standalone: true,
  template: `
    <div style="border:1px solid #ddd; border-radius:12px; padding:12px;">
      <h3 style="margin:0 0 8px;">Tiempo</h3>

      @if (state().loading) {
        <p>Cargando…</p>
      }

      @if (state().error) {
        <p style="color:#b00020;">{{ state().error }}</p>
      }

      @if (!points().length && !state().loading) {
        <p>Sin datos todavía.</p>
      }

      @for (p of points(); track p.address) {
        <div style="margin-top:10px; padding-top:10px; border-top:1px solid #eee;">
          <strong>{{ p.address }}</strong>

          <div style="margin-top:8px; display:grid; gap:6px;">
            @for (row of rowsFor(p); track row.hour) {
              <div style="display:flex; justify-content:space-between; gap:10px;">
                <span>{{ row.hour }}:00</span>
                <span>{{ row.temp }} °C</span>
                <span style="text-align:right; flex:1;">{{ row.desc }}</span>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
})
export class WeatherWidgetComponent {
  private readonly mapState = inject(MapStateService);
  state = this.mapState.state;

  points = computed(() => this.state().weather ?? []);

  // Saca filas ordenadas por hora (y limita a 6 para que no ocupe media vida)
  rowsFor(p: RouteWeatherPoint) {
    const temps = p.temperatures ?? {};
    const descs = p.weatherDescription ?? {};

    const hours = Object.keys(temps)
      .map(h => Number(h))
      .filter(n => Number.isFinite(n))
      .sort((a, b) => a - b)
      .slice(0, 23);

    return hours.map(hour => ({
      hour,
      temp: temps[String(hour)],
      desc: descs[String(hour)] ?? '',
    }));
  }
}