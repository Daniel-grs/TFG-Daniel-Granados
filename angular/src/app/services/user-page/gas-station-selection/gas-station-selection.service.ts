import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GasStationSelectionService {
  selectedStation = signal<string | null>(null);
}