import { Component, signal, inject, effect, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GasStationService } from '../../../../services/user-page/gas-station/gas-station.service';
import { GasStation, FavouriteGasStation } from '../../../../Dto/gas-station';
import { MapPageComponent } from '../../map-page/map-page.component';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { DefaultUserPreferences } from '../../../../Dto/preferences';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { GasStationSelectionService } from '../../../../services/user-page/gas-station-selection/gas-station-selection.service';
import { TranslationService } from '../../../../services/translation.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-user-preferences',
  imports: [CommonModule, FormsModule, MapPageComponent],
  templateUrl: './user-preferences.component.html',
  styleUrl: './user-preferences.component.css'
})
export class UserPreferencesComponent implements OnInit {

  translation = inject(TranslationService);

  private gasStationService = inject(GasStationService);
  private userPreferencesService = inject(UserPreferencesService);
  private gasStationSelectionService = inject(GasStationSelectionService);
  private router = inject(Router);

  defaultUserPreferences: DefaultUserPreferences | null = null;

  fuelType = signal<string>('');
  theme = signal<string>('');
  language = signal<string>('');
  radioKm = signal<number>(0);
  maxPrice = signal<number>(0);
  mapType = signal<string>('');
  avoidTolls = signal<boolean>(false);
  emissionType = signal<string>('');
  preferredBrands = signal<string[]>([]);

  fuelOptions = signal<string[]>([]);
  themeOptions = signal<string[]>([]);
  languageOptions = signal<string[]>([]);
  gasStationBrandsOptions = signal<string[]>([]);
  spainMunicipalities = signal<string[]>([]);
  mapTypeOptions = signal<string[]>([]);
  emissionLabelOptions = signal<string[]>([]);

  selectedStation = this.gasStationSelectionService.selectedStation;
  favoriteGasStations = this.userPreferencesService.getFavoriteGasStationsSignal();
  deletedFavourites = signal<FavouriteGasStation[]>([]);
  newFavorites = signal<FavouriteGasStation[]>([]);
  renamedFavourites = signal<{oldAlias: string, newAlias: string}[]>([]);

  searchAddress = signal<string>('');
  searchResults = signal<GasStation[]>([]);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);

  showRadiusInfo = signal<boolean>(false);
  showGasInfo = signal<boolean>(false);
  showGeneralInfo = signal<boolean>(false);

  isLoadingPreferences = signal<boolean>(true);
  defaultsLoaded = signal<boolean>(false);

  brandSearch = signal<string>('');
  showBrandDropdown = signal<boolean>(false);
  showAddressDropdown = signal<boolean>(false);

  previousMaxPrice = signal<number>(0);

  alias = signal<string>('');

  filteredBrands = computed(() => {
    const search = this.brandSearch().toLowerCase();
    return this.gasStationBrandsOptions().filter(brand => brand.toLowerCase().includes(search)).slice(0, 4);
  });

  filteredMunicipalities = computed(() => {
    const search = this.searchAddress().toLowerCase();
    return this.spainMunicipalities().filter(municipalities => municipalities.toLowerCase().includes(search)).slice(0, 4);
  });

allStations = computed(() =>
  [...this.favoriteGasStations(), ...this.newFavorites(), ...this.searchResults()]
    .filter((s, i, arr) => arr.findIndex(x => x.idEstacion === s.idEstacion) === i)
    .filter(s => s.latitud !== undefined && s.longitud !== undefined)
    .slice(0, 50)
);

  ngOnInit(): void {
    this.loadOptions();

    this.userPreferencesService.getDefaultPreferences().subscribe({
      next: (defaults) => {
        this.defaultUserPreferences = defaults;
        this.defaultsLoaded.set(true);

        this.userPreferencesService.getUserPreferences().subscribe({
          next: (data) => {
            this.preferredBrands.set(data?.preferredBrands ?? defaults?.preferredBrands ?? []);
            this.avoidTolls.set(data?.avoidTolls ?? defaults?.avoidTolls ?? false);
            this.radioKm.set(data?.radioKm ?? defaults?.radioKm ?? 1);
            this.fuelType.set(data?.fuelType ?? defaults?.fuelType ?? 'GASOLINE');
            this.emissionType.set(data?.emissionType ?? defaults?.emissionType ?? 'B');
            this.maxPrice.set(data?.maxPrice ?? defaults?.maxPrice ?? 1.5);
            this.mapType.set(data?.mapView ?? defaults?.mapView ?? 'MAP');
            this.isLoadingPreferences.set(false);
            this.previousMaxPrice.set(data?.maxPrice ?? 0);
          },
          error: (error) => {
            this.isLoadingPreferences.set(false);
            console.error('Error loading user preferences:', error);
          }
        });
      },
      error: (error) => {
        console.error('Error loading default preferences:', error);
        this.defaultsLoaded.set(true);
      }
    });

    this.userPreferencesService.getUserThemeLanguage().subscribe({
      next: (data) => {
        this.theme.set(data.theme || 'LIGHT');
        this.language.set(data.language || 'ES');
      },
      error: (error) => {
        console.error('Error loading user prefs:', error);
      }
    });

    this.userPreferencesService.getUserFavouriteGasStations().subscribe({
      next: (data) => {
        this.favoriteGasStations.set(data || []);
        this.favoriteGasStations().forEach(favorite => {
          if (!favorite.latitud || !favorite.longitud) {
            this.gasStationService.getGasStation(favorite.idEstacion).subscribe({
              next: (fullStation) => {
                this.favoriteGasStations.update(stations =>
                  stations.map(s => s.idEstacion === favorite.idEstacion ? { ...fullStation, alias: favorite.alias } : s)
                );
              },
              error: (err) => {
                console.error('Error obteniendo datos completos de gasolinera favorita:', err);
              }
            });
          }
        });
        this.isLoadingPreferences.set(false);
      },
      error: (error) => {
        this.isLoadingPreferences.set(false);
      }
    });
  }

  loadOptions(): void {
    this.userPreferencesService.getFuelTypes().subscribe(options => this.fuelOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getThemes().subscribe(options => this.themeOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getLanguages().subscribe(options => this.languageOptions.set(options?.map(p => p.code) || []));
    this.gasStationService.getGasStationBrands().subscribe(options => this.gasStationBrandsOptions.set(options || []));
    this.userPreferencesService.getMapTypes().subscribe(options => this.mapTypeOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getEmissionLabels().subscribe(options => this.emissionLabelOptions.set(options?.map(p => p.code) || []));
    this.gasStationService.getMunicipalities().subscribe(municipalities => this.spainMunicipalities.set(municipalities?.map(m => m.nombreMunicipio) || []));
  }

  constructor() {
    effect(() => {
      this.searchAddress();
      this.hasSearched.set(false);
    });

    effect(() => {
      this.brandSearch();
      this.showBrandDropdown.set(this.brandSearch().length > 0);
    });

    effect(() => {
      this.searchAddress();
      this.showAddressDropdown.set(this.searchAddress().length > 0);
    });
  }

  setFuelType(event: Event): void {
    const newFuelType = (event.target as HTMLSelectElement).value;

    if (newFuelType === 'ELECTRIC') {
      this.previousMaxPrice.set(this.maxPrice());
      this.maxPrice.set(5);
    } else if (this.fuelType() === 'ELECTRIC') {
      this.maxPrice.set(this.previousMaxPrice());
    }

    this.fuelType.set(newFuelType);
  }

  setMaxPrice(event: Event): void {
    this.maxPrice.set(parseFloat((event.target as HTMLInputElement).value) || 0);
  }

  setMapType(event: Event): void {
    this.mapType.set((event.target as HTMLSelectElement).value);
  }

  setAvoidTolls(event: Event): void {
    this.avoidTolls.set((event.target as HTMLInputElement).checked);
  }

  setEmissionLabel(event: Event): void {
    this.emissionType.set((event.target as HTMLSelectElement).value);
  }

  setTheme(event: Event): void {
    this.theme.set((event.target as HTMLSelectElement).value);
  }

  setLanguage(event: Event): void {
    this.language.set((event.target as HTMLSelectElement).value);
  }

  setRadioKm(event: Event): void {
    this.radioKm.set(parseInt((event.target as HTMLInputElement).value, 10));
  }

  setSearchAddress(event: Event): void {
    this.searchAddress.set((event.target as HTMLInputElement).value);
  }

  setBrandSearch(event: Event): void {
    this.brandSearch.set((event.target as HTMLInputElement).value);
  }

  setAlias(event: Event): void {
    this.alias.set((event.target as HTMLInputElement).value);
  }

  setSelectedStation(station: GasStation | FavouriteGasStation): void {
    this.selectedStation.set(`${station.nombreEstacion} - ${station.direccion}`);
  }

  setMunicipality(municipality: string): void {
    this.searchAddress.set(municipality);
    this.showAddressDropdown.set(false);
    this.searchGasStations();
  }

  searchGasStations(): void {
    this.searchResults.set([]);
    if (!this.searchAddress().trim()) return;

    this.isLoading.set(true);
    this.hasSearched.set(true);
    const normalizedAddress = this.normalize(this.searchAddress());
    this.gasStationService.getGasStationFromDirectionInRadius(normalizedAddress, this.radioKm()).subscribe({
      next: (results) => {
        this.searchResults.set(results || []);
        this.selectedStation.set(null);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error fetching gas stations:', error);
        alert('Error al buscar gasolineras. Intenta de nuevo.');
        this.isLoading.set(false);
      }
    });
  }

  addSelectedGasStations(): void {
    if (this.selectedStation() && this.alias().trim()) {
      const station = this.searchResults().find(s => `${s.nombreEstacion} - ${s.direccion}` === this.selectedStation());
      if (station && !this.isFavorite(station)) {
        if (this.favoriteGasStations().some(f => f.alias === this.alias()) || this.newFavorites().some(f => f.alias === this.alias())) {
          alert('El alias ya existe. Elige uno diferente.');
          return;
        }
        const favorite: FavouriteGasStation = { ...station, alias: this.alias() };
        this.newFavorites.update(n => [...n, favorite]);
        this.alias.set('');
        this.selectedStation.set(null);
      }
    }
  }

  isFavorite(station: GasStation): boolean {
    return this.favoriteGasStations().some(f => f.idEstacion === station.idEstacion) || this.newFavorites().some(f => f.idEstacion === station.idEstacion);
  }

  toggleGeneralInfo(): void {
    this.showGeneralInfo.update(v => !v);
  }

  toggleInfo(): void {
    this.showRadiusInfo.update(v => !v);
  }

  toggleSelection(station: GasStation): void {
    const key = `${station.nombreEstacion} - ${station.direccion}`;
    if (this.selectedStation() === key) {
      this.selectedStation.set(null);
    } else {
      this.selectedStation.set(key);
    }
  }

  toggleGasInfo(): void {
    this.showGasInfo.update(v => !v);
  }

  savePreferences(): void {

    if (this.isLoadingPreferences() || !this.defaultUserPreferences) {
      alert('Cargando preferencias, intenta de nuevo en unos segundos.');
      return;
    }

    console.log('Sending preferences update with:', {
      radioKm: this.radioKm(),
      fuelType: this.fuelType(),
      emissionType: this.emissionType(),
      maxPrice: this.maxPrice(),
      mapView: this.mapType(),
      avoidTolls: this.avoidTolls(),
      preferredBrands: this.preferredBrands()
    });

    const updatePrefs$ = this.userPreferencesService.updateUserPreferences(
      this.radioKm(), this.fuelType(), this.emissionType(), this.maxPrice(), this.mapType(), this.avoidTolls(), this.preferredBrands()
    );

    const updateTheme$ = this.userPreferencesService.updateUserThemeLanguage(
      this.theme(), this.language()
    );

    const updateFavorites$ = this.newFavorites().map(f =>
      this.userPreferencesService.updateFavouriteGasStations(f.alias, f.idEstacion)
    );

    const deleteFavorites$ = this.deletedFavourites().map(f =>
      this.userPreferencesService.deleteFavouriteGasStations(f.alias)
    );

    const renameFavorites$ = this.renamedFavourites().map(r =>
      this.userPreferencesService.renameFavouriteGasStations(r.oldAlias, r.newAlias)
    );

    console.log('Sending theme/language update with:', {
      theme: this.theme(),
      language: this.language()
    });

    forkJoin([updatePrefs$, updateTheme$, ...updateFavorites$, ...deleteFavorites$, ...renameFavorites$]).subscribe({
      next: () => {
        console.log('All updates completed');
        this.newFavorites.set([]);
        this.deletedFavourites.set([]);
        this.renamedFavourites.set([]);
        history.scrollRestoration = 'manual';
        window.scrollTo(0, 0);
        this.router.navigate(['/user-preferences'])
      },
      error: (err) => {
        console.error('Error saving preferences:', err);
        alert('Error saving preferences: ' + (err?.message || 'Unknown error'));
        this.newFavorites.set([]);
        this.deletedFavourites.set([]);
        this.renamedFavourites.set([]);
      }
    });
  }

  resetPreferences(): void {
    if (this.isLoadingPreferences() || !this.defaultUserPreferences) {
      alert('Cargando preferencias, intenta de nuevo en unos segundos.');
      return;
    }
    
    this.fuelType.set(this.defaultUserPreferences.fuelType || 'GASOLINE');
    this.theme.set('LIGHT');
    this.language.set('ES');
    this.radioKm.set(this.defaultUserPreferences.radioKm || 1);
    this.maxPrice.set(this.defaultUserPreferences.maxPrice || 1.5);
    this.mapType.set(this.defaultUserPreferences.mapView || 'MAP');
    this.avoidTolls.set(this.defaultUserPreferences.avoidTolls || false);
    this.emissionType.set(this.defaultUserPreferences.emissionType || 'B');
    this.preferredBrands.set(this.defaultUserPreferences.preferredBrands || []);
    this.newFavorites.set([]);
    this.deletedFavourites.set([]);
    this.renamedFavourites.set([]);

    console.log('Resetting to defaults:', this.defaultUserPreferences);
    console.log('Values being sent:');

    const updatePrefs$ = this.userPreferencesService.updateUserPreferences(
      this.radioKm(), this.fuelType(), this.emissionType(), this.maxPrice(), this.mapType(), this.avoidTolls(), this.preferredBrands()
    );

    const updateTheme$ = this.userPreferencesService.updateUserThemeLanguage(
      this.theme(), this.language()
    );

    forkJoin([updatePrefs$, updateTheme$]).subscribe({
      next: () => {
        history.scrollRestoration = 'manual';
        window.scrollTo(0, 0);
        this.router.navigate(['/user-preferences']);
      },
      error: (err) => {
        console.error('Error resetting preferences:', err);
        alert('Error al reiniciar: ' + (err?.message || 'Error desconocido'));
      }
    });
  }

  removeFavoriteGasStation(station: { idEstacion: number }): void {
    const favorite = this.favoriteGasStations().find(f => f.idEstacion === station.idEstacion);
    if (favorite) {
      this.favoriteGasStations.update(stations => stations.filter(s => s.idEstacion !== station.idEstacion));
      this.deletedFavourites.update(deleted => [...deleted, favorite]);
    } else {
      const newFav = this.newFavorites().find(f => f.idEstacion === station.idEstacion);
      if (newFav) {
        this.newFavorites.update(n => n.filter(s => s.idEstacion !== station.idEstacion));
      }
    }
  }

  renameFavoriteGasStation(station: FavouriteGasStation): void {
    const newAlias = prompt('Ingresa el nuevo alias para la gasolinera:', station.alias);
    if (newAlias && newAlias.trim() && newAlias.trim() !== station.alias) {
      if (this.favoriteGasStations().some(f => f.alias === newAlias.trim()) || this.newFavorites().some(f => f.alias === newAlias.trim())) {
        alert('El alias ya existe. Elige uno diferente.');
        return;
      }
      this.favoriteGasStations.update(stations => stations.map(s => s.alias === station.alias ? { ...s, alias: newAlias.trim() } : s));
      this.renamedFavourites.update(r => [...r, { oldAlias: station.alias, newAlias: newAlias.trim() }]);
    }
  }

  addPreferredBrand(brand: string): void {
    if (!this.preferredBrands().includes(brand)) {
      this.preferredBrands.update(brands => [...brands, brand]);
    }
    this.brandSearch.set('');
  }

  removePreferredBrand(brand: string): void {
    this.preferredBrands.update(brands => brands.filter(b => b !== brand));
  }

  normalize(text: string): string {
    return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  capitalize(str: string): string {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
  }
}