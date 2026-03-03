export interface User {
    email: string,
    password: string,
    passwordConfirmation: string,
    name: string,
    surname: string
}
export interface UserLoginDTO{
  user: string,
  password: string
}
export interface UserDto {
  id: number;
  email: string;
  password: string;
  passwordConfirmation: string;
  name: string;
  surname: string;
  userPreferences: UserPreferencesDto;
  savedRoutes: SavedRouteDto[];
  favouriteGasStations: FavouriteGasStationDto[];
  gasStationPriority: GasStationPriority;
  routePreferences: RoutePreferencesDto;
}
export interface UserPreferencesDto {
  theme: string;
  language: string;
  user: string;
}
export interface SavedRouteDto {
  routeId: string;
  name: string;
  puntos: RoutePointDto[];
  user: string;
  optimizeWaypoints: boolean;
  optimizeRoute: boolean;
  language: string;
}
export interface RoutePointDto {
  id: number;
  type: RoutePointType;
  address: string;
  savedRoute: string;
}
export type RoutePointType = 'ORIGIN' | 'DESTINATION' | 'WAYPOINT';

export interface FavouriteGasStationDto {
  idEstacion: number;
  nombreEstacion: string;
  marca: string;
  horario: string;
  longitud: number;
  latitud: number;
  direccion: string;
  localidad: string;
  idMunicipio: number;
  codPostal: number;
  provincia: string;
  provinciaDistrito: string;
  tipoVenta: string;
  lastUpdate: string;
  Gasolina95: number;
  Gasolina95_media: number;
  Gasolina98: number;
  Gasolina98_media: number;
  Diesel: number;
  Diesel_media: number;
  DieselPremium: number;
  DieselPremium_media: number;
  DieselB: number;
  DieselB_media: number;
  GLP: number;
  GLP_media: number;
}

export interface RoutePreferencesDto {
  preferredBrands: string[];
  radioKm: number;
  fuelType: string;
  maxPrice: number;
  mapView: MapView;
}

export type MapView = 'SATELLITE' | 'SCHEMATIC';
export type GasStationPriority = 'PRICE' | 'DISTANCE' | 'RATING';

