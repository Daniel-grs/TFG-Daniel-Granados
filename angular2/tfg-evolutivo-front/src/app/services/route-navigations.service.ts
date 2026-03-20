import { Injectable, signal } from '@angular/core';
import { RouteRequest } from '../models/route-request';

@Injectable({ providedIn: 'root' })
export class RouteNavigationService {
  private readonly _pendingRoute = signal<RouteRequest | null>(null);

  readonly pendingRoute = this._pendingRoute.asReadonly();

  setPendingRoute(route: RouteRequest) {
    this._pendingRoute.set(route);
  }

  clearPendingRoute() {
    this._pendingRoute.set(null);
  }
}