import { Injectable, signal } from '@angular/core';
import esTranslations from '../../assets/i18n/es.json';
import enTranslations from '../../assets/i18n/en.json';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {
  private translationsMap = {
    ES: esTranslations,
    EN: enTranslations
  };

  private translations = signal<Record<string, string>>({});
  private currentLang = signal<string>('ES');

  constructor() {
    const savedLang = localStorage.getItem('selectedLanguage') || 'ES';
    this.setLanguage(savedLang);
  }

  setLanguage(lang: string): void {
    const data = this.translationsMap[lang as keyof typeof this.translationsMap];
    if (data) {
      const flatData = this.flattenObject(data);
      this.translations.set(flatData);
      this.currentLang.set(lang);
      localStorage.setItem('selectedLanguage', lang);
    }
  }

  private flattenObject(obj: any, prefix = ''): Record<string, string> {
    let result: Record<string, string> = {};
    for (const key in obj) {
      if (typeof obj[key] === 'object' && obj[key] !== null) {
        result = { ...result, ...this.flattenObject(obj[key], prefix + key + '.') };
      } else {
        result[prefix + key] = obj[key];
      }
    }
    return result;
  }

  translate(key: string): string {
    return this.translations()[key] || key;
  }

  getCurrentLang(): string {
    return this.currentLang();
  }

  getTranslations() {
    return this.translations;
  }
}