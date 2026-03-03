import { UserPreferencesDto } from "./user-dtos"

export interface SavedRoute {
    routeId: number,
    name: string,
    points: Point[],
    preferences: UserPreferencesDto
}
export interface Point {
    type: string,
    address: string
}