export interface Preferences {
    code: string,
    label: string
}

export interface DefaultUserPreferences {
  avoidTolls: boolean,
  emissionType: string,
  fuelType: string,
  mapView: string,
  maxPrice: number,
  preferredBrands: [],
  radioKm: number
}

export interface ThemeLangPreferences {
    language: string,
    theme: string
}