import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FavouriteGasStationDto, User, UserLoginDTO } from '../../Dto/user-dtos';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SavedRoute } from '../../Dto/saved-route';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUrl = 'http://localhost:8080/api/users';
  private routeUrl = 'http://localhost:8080/api/savedRoute'
  constructor(private http: HttpClient) { }

  receiveUserData(): Observable<string> {
    return this.http.get(this.userUrl + "/get", { responseType: 'text', withCredentials: true });
  }

  receiveFavouriteGasStations(): Observable<string> {
    return this.http.get(this.userUrl + "/favouriteStations", { responseType: 'text', withCredentials: true });
  }

  receiveSavedRoutes(): Observable<string> {
    return this.http.get(this.routeUrl, {responseType: 'text', withCredentials: true });
  }

}
