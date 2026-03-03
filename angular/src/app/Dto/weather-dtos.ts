export interface WeatherData {
  address: string;
  weatherDescription: { [key: string]: string };
  temperatures: { [key: string]: number };
}