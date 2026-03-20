export type HourString = string; // "7", "8", ...

export interface RouteWeatherPoint {
  address: string;
  weatherDescription: Record<HourString, string>;
  temperatures: Record<HourString, number>;
}