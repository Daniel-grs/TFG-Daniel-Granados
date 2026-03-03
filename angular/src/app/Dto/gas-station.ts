export interface GasStation {
  idEstacion: number;
  nombreEstacion: string;
  marca: string;
  horario: string;
  lastUpdate: string;
  longitud: number;
  latitud: number;
  direccion: string;
  localidad: string;
  idMunicipio: number | null;
  codPostal: number;
  provincia: string;
  provinciaDistrito: string;
  tipoVenta: string;
  Gasolina95: number | null;
  Gasolina95_media: number | null;
  Gasolina98: number | null;
  Gasolina98_media: number | null;
  Diesel: number | null;
  Diesel_media: number | null;
  DieselB: number | null;
  DieselB_media: number | null;
  DieselPremium: number | null;
  DieselPremium_media: number | null;
  GLP: number | null;
  GLP_media: number | null;
}

export interface FavouriteGasStation extends GasStation {
  alias: string;
}