export interface Point {
  address: string;
  type: 'ORIGIN' | 'WAYPOINT' | 'DESTINATION' | string;
}