import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output, computed, signal, effect, inject } from '@angular/core';
import { WeatherData } from '../../../../Dto/weather-dtos';
import { TranslationService } from '../../../../services/translation.service';

@Component({
  selector: 'app-weather-overlay',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './weather-overlay.component.html',
  styleUrls: ['./weather-overlay.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WeatherOverlayComponent {

  translation = inject(TranslationService);

  data = input<WeatherData[] | null>();
  close = output<void>();

  currentHour = signal<number>(new Date().getHours());
  currentHourStr = computed(() => this.currentHour().toString());

  constructor() {
    effect(() => {
      const timer = setInterval(() => {
        this.currentHour.set(new Date().getHours());
      }, 60_000);
      return () => clearInterval(timer);
    });
  }

  hourEntries(obj: { [key: string]: any } | null): Array<[string, any]> {
    if (!obj) return [];
    return Object.entries(obj);
  }

  getForHour(obj: { [key: string]: any } | null, hourStr: string | null): any | null {
    if (!obj || hourStr == null) return null;
    return obj.hasOwnProperty(hourStr) ? obj[hourStr] : null;
  }

  hasForHour(obj: { [key: string]: any } | null, hourStr: string | null): boolean {
    if (!obj || hourStr == null) return false;
    return obj.hasOwnProperty(hourStr);
  }

  translateWeatherDesc(desc: string): string {
    const key = 'weatherTerms.' + desc;
    const translated = this.translation.translate(key);
    return translated !== key ? translated : desc;
  }

}