import { GasStation } from './gas-station';

export interface CoordsWithStations {
  coordsList: { lat: number; lng: number }[];
  stations: GasStation[];
}