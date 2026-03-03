export interface RouteFormResponse {
    origin: string,
    destination: string,
    waypoints: string [],
    optimizeWaypoints: boolean,
    optimizeRoute: boolean,
    avoidTolls: boolean,
    vehiculeEmissionType: string,
    radioKm?: number;
}
