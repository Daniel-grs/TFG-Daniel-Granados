import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';

import { environment } from '../../environments/environment';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  login(body: LoginRequest) {
    return this.http.post<void>(this.baseUrl + '/auth/login', body, {
      withCredentials: true,
    });
  }

  register(body: RegisterRequest) {
    return this.http.post<void>(this.baseUrl + '/auth/register', body, {
      withCredentials: true,
    });
  }

  logout() {
    return this.http.post<void>(this.baseUrl + '/auth/logout', {}, {
      withCredentials: true,
    });
  }

  check() {
    return this.http.post<void>(this.baseUrl + '/auth/check', {}, {
      withCredentials: true,
    }).pipe(
      map(() => true)
    );
  }
}