import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of } from 'rxjs';
import { TranslationService } from '../services/translation.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    private baseUrl = 'http://localhost:8080';

    constructor(private router: Router, private http: HttpClient, private translation: TranslationService) { }

    canActivate(): Observable<boolean> {
        return this.http.post(`${this.baseUrl}/auth/check`, {}, { withCredentials: true }).pipe(
            map(() => true),
            catchError(() => {
                alert(this.translation.translate('auth.loginRequired'));
                this.router.navigate(['/login']);
                return of(false);
            })
        );

    }

}