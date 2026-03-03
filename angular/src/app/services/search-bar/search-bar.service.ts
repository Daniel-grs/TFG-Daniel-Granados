import { Injectable, TemplateRef } from '@angular/core';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { RouteService } from '../routes/route.service';
import { Coords, RouteGroupResponse } from '../../Dto/maps-dtos';
import { MapCommunicationService } from '../map/map-communication.service';
import { WeatherData } from '../../Dto/weather-dtos';
import { FavouriteGasStationDto, UserDto } from '../../Dto/user-dtos';
import { UserService } from '../user/user.service';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class SearchBarService {

  constructor(private routeService: RouteService, private mapCommunication: MapCommunicationService, private userService: UserService) { }

  async onSubmit(routeFormResponse: RouteFormResponse) {
    await this.saveCoordinates(routeFormResponse);
    await this.saveWaypointCoordinates(routeFormResponse);

    await this.saveGasStationsCoordinates(routeFormResponse);
    await this.saveWeatherRoute(routeFormResponse);
  }

  saveCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculatePolylineCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);

      coords = parsedData;
      this.giveCoords(coords)
    })
  }
  saveWaypointCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculatePointCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);
      coords = parsedData;
      this.giveWaypointCoords(coords)
    })

  }
  saveGasStationsCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []
    this.routeService.calculateGasStations(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);
      coords = parsedData;
      console.log(coords)
      this.giveGasStationCoords(coords)
    })

  }

  saveWeatherRoute(routeFormResponse: RouteFormResponse){
    let weather: WeatherData []

    this.routeService.calculateWeatherRoute(routeFormResponse).subscribe(data =>{
      weather = JSON.parse(data);
      console.log(weather)
      this.giveWeatherCoords(weather)
    })
  }

  saveUserData(): Observable<string> {
    return this.userService.receiveUserData();
  }
  saveFavouriteGasStations(): Observable<string>{
    return this.userService.receiveFavouriteGasStations()
  }
  saveSavedRoutes(): Observable<string>{
    return this.userService.receiveSavedRoutes()
  }



  saveFavouriteRoute(alias: string, routeFormResponse: RouteFormResponse) {
    return this.routeService.saveFavouriteRoute(alias, routeFormResponse)
  }
  giveCoords(coords: Coords[]){
    this.mapCommunication.sendRoute(coords)
  }
  giveWaypointCoords(coords: Coords[]){
    this.mapCommunication.sendPoints(coords);
  }
  giveGasStationCoords(coords: Coords[]){
    this.mapCommunication.sendGasStations(coords)
  }
  giveWeatherCoords(weather: WeatherData []){
    this.mapCommunication.sendWeather(weather);
  }
}
