import { Coords } from './coords';

export interface WeatherHour {
  // Campos típicos; si el back trae otros, no rompe
  time?: string;        // ISO o texto
  temp?: number;        // ºC
  description?: string; // “nubes”, etc.
  icon?: string;
}

export interface CoordsWithWeather {
  coords: Coords;
  weather?: WeatherHour[];
}