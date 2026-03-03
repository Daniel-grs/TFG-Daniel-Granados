import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  // Signal para el tema actual
  private currentTheme = signal<string>('LIGHT');

  constructor() {
    // Carga desde localStorage
    const savedTheme = localStorage.getItem('selectedTheme') || 'LIGHT';
    this.setTheme(savedTheme);
  }

  // Aplica el tema (agrega clase al body)
  setTheme(theme: string): void {
    this.currentTheme.set(theme);
    localStorage.setItem('selectedTheme', theme);
    document.body.className = theme.toLowerCase(); // Agrega clase 'light' o 'dark' al body
  }

  // Obtiene el tema actual
  getCurrentTheme(): string {
    return this.currentTheme();
  }

  // Alterna entre LIGHT y DARK
  toggleTheme(): void {
    const newTheme = this.currentTheme() === 'LIGHT' ? 'DARK' : 'LIGHT';
    this.setTheme(newTheme);
  }
}