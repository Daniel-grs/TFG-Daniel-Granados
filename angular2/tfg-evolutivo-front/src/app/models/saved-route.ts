import { Point } from './point';

export interface SavedRoutePreferences {
  optimizeWaypoints?: boolean;
  optimizeRoute?: boolean;
  language?: string;
  avoidTolls?: boolean;
  vehicleEmissionType?: string;
}

export interface SavedRoute {
  routeId: number;
  name: string;
  points: Point[];
  preferences?: SavedRoutePreferences;
}