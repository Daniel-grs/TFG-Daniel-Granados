export interface RouteWeatherPoint {
  address: string;
  estimatedArrivalHour: number | null;
  weatherDescription: string;
  temperature: number | null;
}