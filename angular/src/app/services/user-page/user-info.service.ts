import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, ReplaySubject } from 'rxjs';
import { map, catchError, shareReplay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserInfoService {
  private baseUrl = 'http://localhost:8080';

  private userSignal = signal<any>({});
  private routesSignal = signal<any[]>([]);
  private loggedInSubject = new ReplaySubject<boolean>(1);
  private checked = false;

  constructor(private http: HttpClient) { }

  isLoggedIn(): Observable<boolean> {
    if (this.checked) {
      return this.loggedInSubject.asObservable();
    }
    this.checked = true;
    const stored = localStorage.getItem('isLoggedIn');
    if (stored === 'true') {
      this.loggedInSubject.next(true);
      return this.loggedInSubject.asObservable();
    }
    return this.http.post<boolean>(`${this.baseUrl}/auth/check`, {}, { withCredentials: true }).pipe(
      map(() => {
        localStorage.setItem('isLoggedIn', 'true');
        this.loggedInSubject.next(true);
        return true;
      }),
      catchError(() => {
        localStorage.removeItem('isLoggedIn');
        this.loggedInSubject.next(false);
        return of(false);
      }),
      shareReplay(1)
    );
  }

  getUserSignal() { return this.userSignal; }
  setUser(data: any) { this.userSignal.set(data); }
  getRoutesSignal() { return this.routesSignal; }
  setRoutes(data: any[]) { this.routesSignal.set(data); }

  getUserInfo(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/users/get`, { withCredentials: true });
  }

  getUserRoutes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/savedRoute`, { withCredentials: true });
  }

  executeRoute(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/savedRoute/execute`, {
      params: { id: id.toString() },
      withCredentials: true
    });
  }

  deleteRoute(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/savedRoute/delete/${id}`, {
      params: { id }, withCredentials: true });
  }

  renameRoute(routeId: number, newName: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/savedRoute/rename`, null, {
      params: { routeId: routeId.toString(), newName },
      withCredentials: true
    });
  }
}