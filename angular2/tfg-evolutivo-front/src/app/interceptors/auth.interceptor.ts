import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthStateService } from '../services/auth-state.service';
import { MapStateService } from '../services/map-state.service';
import { UserPreferencesStateService } from '../services/user-preferences-state.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authState = inject(AuthStateService);
  const mapState = inject(MapStateService);
  const userPreferencesState = inject(UserPreferencesStateService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        authState.markLoggedOut();
        mapState.clear();
        userPreferencesState.clearPreferences();

        // evita redirecciones raras si ya estás en login/register
        const currentUrl = router.url;
        if (currentUrl !== '/login' && currentUrl !== '/register') {
          router.navigateByUrl('/login', {
            state: { sessionExpired: true },
          });
        }
      }

      return throwError(() => error);
    })
  );
};