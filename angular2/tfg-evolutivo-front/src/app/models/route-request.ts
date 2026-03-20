export type VehicleEmissionType = 'C' | 'ELECTRIC' | 'HYBRID' | 'GASOLINE' | 'DIESEL';

export interface RouteRequest {
  origin: string;
  destination: string;

  waypoints?: string[];

  optimizeWaypoints?: boolean;
  optimizeRoute?: boolean;

  language?: string;          // default "es" en back
  avoidTolls?: boolean;

  vehicleEmissionType?: VehicleEmissionType; // default "C" en back

  radius?: number; // para /api/routes/gasStations (requerido por back)
}