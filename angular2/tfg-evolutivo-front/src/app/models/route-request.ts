export type VehicleEmissionType = 'C' | 'ELECTRIC' | 'HYBRID' | 'GASOLINE' | 'DIESEL';

export interface RouteRequest {
  origin: string;
  destination: string;

  waypoints?: string[];

  optimizeWaypoints?: boolean;
  optimizeRoute?: boolean;

  language?: string;
  avoidTolls?: boolean;

  radius?: number;
}