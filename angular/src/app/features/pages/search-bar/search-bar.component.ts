import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { KeyValuePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../../../Dto/maps-dtos';
import { FavouriteGasStation } from '../../../Dto/gas-station';
import { SavedRoute } from '../../../Dto/saved-route';
import { TranslationService } from '../../../services/translation.service';
import { GasStation } from '../../../Dto/gas-station';
import { catchError } from 'rxjs/operators';
import { UserInfoService } from '../../../services/user-page/user-info.service';
import { UserPreferencesService } from '../../../services/user-page/user-preferences.service';
import { of } from 'rxjs';
import { AuthService } from '../../../services/auth/auth-service.service';

@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent, NgFor, KeyValuePipe, NgIf, NgClass],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {

  isLoggedIn = signal<boolean>(false);

  translation = inject(TranslationService);
  userInfo = inject(UserInfoService);
  userPreferences = inject(UserPreferencesService);

  favouriteGasStations = signal<FavouriteGasStation[]>([]);
  savedRoute = signal<SavedRoute[]>([]);
  filteredGasStations = signal<GasStation[]>([]);
  preferredBrands: string[] = [];
  fuelType: string = 'ALL';
  maxPrice: number = 0;
  radioKm: number = 2;
  destinationType: string = "";
  routeAlias: string = "";
  selectedRouteOption = {
    origin: "",
    destination: ""
  };

  routeFormResponse: RouteFormResponse = {
    origin: "",
    destination: "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false,
    vehiculeEmissionType: "NONE"
  };
  waypointTypes: string[] = [];

  destinationTypeOptions: Record<string, string> = {
    coordinates: "Destino",
    favouriteGasStations: "Gasolinera favorita",
    savedRoute: "Ruta guardada"
  };

  vehiculeEmissionTypeOptions: Record<string, string> = {
    ELECTRIC: "Eléctrico",
    HYBRID: "Híbrido",
    DIESEL: 'Diesel',
    GASOLINE: "Gasolina"
  };

  activeTab: string = 'destination';

  constructor(private searchBarService: SearchBarService, private routeService: RouteService, private authService: AuthService) { }

  ngOnInit(): void {
    this.userInfo.isLoggedIn().subscribe(logged => {
      this.authService.sendUserSession(logged);
    });

    this.authService.getUserSession().subscribe(logged => {
      this.isLoggedIn.set(logged);
      if (logged) {
        this.userPreferences.getUserPreferences().subscribe(pref => {
          this.routeFormResponse.vehiculeEmissionType = pref.emissionType;
          this.routeFormResponse.avoidTolls = pref.avoidTolls;
          this.preferredBrands = pref.preferredBrands;
          this.fuelType = pref.fuelType;
          this.maxPrice = pref.maxPrice;
          this.radioKm = pref.radioKm;
          this.routeFormResponse.radioKm = pref.radioKm;
        });
        this.searchBarService
          .saveFavouriteGasStations()
          .subscribe(gas => this.favouriteGasStations.set(JSON.parse(gas))
          );
        this.searchBarService
          .saveSavedRoutes()
          .subscribe(route => {
            this.savedRoute.set(JSON.parse(route));
          });
      } else {
        this.favouriteGasStations.set([]);
        this.savedRoute.set([]);
        this.fuelType = 'ALL';
        this.maxPrice = 0;
        this.radioKm = 2;
        this.routeFormResponse.radioKm = 2;
      }
    });
  }

  setTab(tab: string) {
    if (tab === 'gas' || tab === 'route') {
      if (!this.isLoggedIn()) {
        alert(this.translation.translate('auth.loginRequired'));
        return;
      }
    }
    this.activeTab = tab;
  }

  addWaypoint() {
    this.routeFormResponse.waypoints.push('');
    this.waypointTypes.push('text');
  }

  deleteWaypoint() {
    this.routeFormResponse.waypoints.pop();
    this.waypointTypes.pop();
  }

  savedRouteSelected() {
    this.routeFormResponse.origin = this.selectedRouteOption.origin;
    this.routeFormResponse.destination = this.selectedRouteOption.destination;
  }

  submitted: boolean = false;
  onSubmit() {
    this.searchBarService.onSubmit(this.routeFormResponse);
    this.submitted = true;

    this.routeService.calculateGasStations(this.routeFormResponse).subscribe(coordsStr => {
      const coords: { lat: number, lng: number }[] = JSON.parse(coordsStr);
      const allStationsMap = new Map<number, GasStation>();
      const promises = coords.map(coord =>
        this.routeService.getGasStationsByCoords(coord.lat, coord.lng, this.radioKm).toPromise()
      );
      Promise.all(promises).then(results => {
        results.forEach(stations => {
          if (stations) {
            stations.forEach(station => {
              allStationsMap.set(station.idEstacion, station);
            });
          }
        });
        const allStations = Array.from(allStationsMap.values());
        let filtered = allStations;

        if (this.preferredBrands.length > 0) {
          filtered = filtered.filter(station => 
            this.preferredBrands.some(brand => brand.toLowerCase() === station.marca.toLowerCase())
          );
        }

        if (this.fuelType !== 'ELECTRIC') {
          const fuelKey = (this.fuelType === 'ALL' || this.fuelType === 'GASOLINE') ? 'Gasolina95' : 'Diesel';
          filtered = filtered.filter(station => {
            const price = station[fuelKey];
            return price != null && price <= this.maxPrice;
          });
        }

        this.filteredGasStations.set(filtered);
      }).catch(err => {
        console.error('Error fetching gas stations:', err);
      });
    });
  }

  trackByIndex(index: number) {
      return index;
    }

  successfulMessage: string = "";
    errorMessage: string = "";
    saveRoute() {
      this.searchBarService.saveFavouriteRoute(this.routeAlias, this.routeFormResponse)
        .subscribe({
          next: (response) => {
            this.successfulMessage = this.translation.translate('search.routeSaved');
            this.errorMessage = "";
            console.log(response);
          }, error: (err) => {
            this.errorMessage = this.translation.translate('search.saveError');
            this.successfulMessage = "";
            console.log(err);
          },
        });
    }
  }