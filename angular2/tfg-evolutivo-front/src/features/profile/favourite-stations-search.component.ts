import { Component, inject, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { UserService } from '../../app/services/user.service';
import { GasStation } from '../../app/models/gas-station';

@Component({
  selector: 'app-favourite-stations-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="card">
      <h2>Buscar gasolineras</h2>

      <div class="form">
        <input [(ngModel)]="location" placeholder="Ubicación (ej: Mejorada del Campo)" />
        <input [(ngModel)]="radius" type="number" placeholder="Radio (km)" />
        <input [(ngModel)]="brandFilter" placeholder="Filtrar por marca (opcional)" />

        <button (click)="search()">Buscar</button>
      </div>

      @if (loading()) {
        <p>Cargando...</p>
      }

      @if (stations().length > 0) {
        <div class="list">
          @for (station of filteredStations(); track station.idEstacion) {
            <div class="item">
              <div>
                <strong>{{ station.nombreEstacion }}</strong>
                <p>{{ station.marca }}</p>
                <p class="muted">{{ station.direccion }}</p>
              </div>

              <button (click)="save(station)">Guardar</button>
            </div>
          }
        </div>
      }
    </section>
  `,
  styles: [`
    .form{
      display:flex;
      gap:8px;
      flex-wrap:wrap;
      margin-bottom:12px;
    }
    .list{
      display:grid;
      gap:10px;
    }
    .item{
      display:flex;
      justify-content:space-between;
      border:1px solid #ddd;
      padding:10px;
      border-radius:8px;
    }
    .muted{
      color:#666;
      font-size:12px;
    }
  `]
})
export class FavouriteStationsSearchComponent {
  private userService = inject(UserService);

  location = '';
  radius = 5;
  brandFilter = '';

  loading = signal(false);
  stations = signal<GasStation[]>([]);
  favouriteSaved = output<void>();

  async search() {
    if (!this.location) return;

    this.loading.set(true);

    try {
      const geocoder = new google.maps.Geocoder();
      const result = await geocoder.geocode({ address: this.location });

      const lat = result.results[0].geometry.location.lat();
      const lng = result.results[0].geometry.location.lng();

      this.userService.searchGasStations(lat, lng, this.radius).subscribe({
        next: (res) => {
          console.log('SEARCH GAS RESPONSE', res);
          this.stations.set(res ?? []);
          this.loading.set(false);
        },
        error: () => {
        alert('Error buscando gasolineras');
        this.stations.set([]);
        this.loading.set(false);
      }
      });

    } catch {
      alert('No se pudo geocodificar la ubicación');
      this.stations.set([]);
      this.loading.set(false);
    }
  }

  filteredStations() {
  const brand = this.brandFilter.toLowerCase();
  const stations = this.stations() ?? [];

  return stations.filter(s =>
    !brand || s.marca?.toLowerCase().includes(brand)
  );
}

  save(station: GasStation) {
    const alias = prompt('Pon un alias para la gasolinera:', station.nombreEstacion);
    if (!alias || !alias.trim()) return;

    this.userService.saveFavouriteStation(alias.trim(), station.idEstacion).subscribe({
    next: () => {
      alert('Guardada en favoritas');
      this.resetSearchState();
      this.favouriteSaved.emit();
    },
    error: () => alert('Error al guardar'),
  });
}
  private resetSearchState() {
  this.location = '';
  this.brandFilter = '';
  this.stations.set([]);
  this.loading.set(false);
}
}