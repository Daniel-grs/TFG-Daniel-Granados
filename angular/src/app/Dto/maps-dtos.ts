export interface RouteGroupResponse {
  routes?: Route[];
}

export interface Route {
  legs?: Leg[];
}

export interface Leg {
  distance?: Distance;
  duration?: Duration;
  end_address?: string;
  end_location?: Coords;
  start_address?: string;
  start_location?: Coords;
  steps?: Step[];
}

export interface Step {
  polyline?: Polyline
  start_location?: Coords;
}

export interface Polyline {
  points?: String;
}

export interface Coords { 
  lat: number; 
  lng: number; 
}

export interface Distance { 
  text: string; 
  value: number; 
}

export interface Duration { 
  text: string; 
  value: number; 
}