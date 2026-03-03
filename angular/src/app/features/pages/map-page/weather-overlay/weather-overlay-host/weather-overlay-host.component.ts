import {
  Component,
  input,
  output,
  OnDestroy,
  EnvironmentInjector,
  ApplicationRef,
  ComponentRef,
  Renderer2,
  createComponent,
  effect,
  inject,
  OutputRefSubscription
} from '@angular/core';
import { WeatherOverlayComponent } from '../weather-overlay.component';
import { WeatherData } from '../../../../../Dto/weather-dtos';

@Component({
  selector: 'app-weather-overlay-host',
  template: '',
  standalone: true,
  styleUrls: ['./weather-overlay-host.component.css']
})
export class WeatherOverlayHostComponent implements OnDestroy {
  mapDiv = input<HTMLElement | null>(null);
  weatherRoute = input<WeatherData[] | null>(null);
  createdRoute = input<boolean>(false);
  visibleChange = output<void>();

  private overlayRef: ComponentRef<WeatherOverlayComponent> | null = null;
  private overlayCloseSub: OutputRefSubscription | null = null;
  private hostDiv: HTMLElement | null = null;
  private tabEl: HTMLElement | null = null;

  private latestData: WeatherData[] | null = null;
  private visible = false;

  private readonly environmentInjector = inject(EnvironmentInjector);
  private readonly appRef = inject(ApplicationRef);
  private readonly renderer = inject(Renderer2);

  private readonly fullscreenSelectors = [
    '.gm-fullscreen-control',
    '[title*="Fullscreen"]',
    '[aria-label*="fullscreen"]',
    '[title*="pantalla completa"]',
    '[aria-label*="pantalla completa"]'
  ];

  constructor() {
    effect(() => {
      this.latestData = this.weatherRoute() ?? null;
      this.ensureMountedIfPossible();
    });

    effect(() => {
      this.mapDiv();
      this.ensureMountedIfPossible();
    });
  }

  ngOnDestroy(): void {
    this.stopListeners();
    this.destroyOverlay();
    this.removeTab();
  }

  private async ensureMountedIfPossible(): Promise<void> {
    const mapDiv = this.mapDiv();
    if (!mapDiv) return;

    const ready = await this.waitForElementConnected(mapDiv, 1500);
    if (!ready) return;

    const parent = this.resolveTargetParent();
    if (!parent) return;

    this.ensureTabInParent(parent);

    if (this.hostDiv && this.hostDiv.parentElement === parent && this.overlayRef) {
      this.applyData();
      this.applyVisibility();
      this.schedulePosition(80);
      return;
    }

    await this.recreateOverlayInParent(parent);
    this.schedulePosition(120);
    this.startListeners();
  }

  private resolveTargetParent(): HTMLElement | null {
    const fsEl = (document.fullscreenElement ||
      (document as any).webkitFullscreenElement ||
      (document as any).mozFullScreenElement ||
      (document as any).msFullscreenElement) as HTMLElement | null;
    if (fsEl) return fsEl;
    const mapWrapper = document.getElementById('map') as HTMLElement | null;
    if (mapWrapper?.parentElement) return mapWrapper.parentElement;
    return this.mapDiv();
  }

  private recreateOverlayInParent(targetParent: HTMLElement): Promise<void> {
    this.destroyOverlay();

    const div = this.renderer.createElement('div') as HTMLElement;
    div.className = 'weather-overlay-host__container weather-overlay-host__container--hidden';
    targetParent.appendChild(div);
    this.hostDiv = div;

    this.overlayRef = createComponent(WeatherOverlayComponent, {
      environmentInjector: this.environmentInjector,
      hostElement: div
    });
    this.appRef.attachView(this.overlayRef.hostView);

    this.overlayCloseSub = this.overlayRef.instance.close.subscribe(() => {
      this.visible = false;
      this.applyVisibility();
      this.visibleChange.emit();
    });

    this.applyData();
    this.applyVisibility();

    this.hostDiv.classList.remove('weather-overlay-host__container--hidden');
    if (this.tabEl) this.tabEl.classList.remove('weather-tab-container--hidden');

    this.positionLeftOfFullscreenControl();

    return Promise.resolve();
  }

  private destroyOverlay(): void {
    this.overlayCloseSub?.unsubscribe();
    this.overlayCloseSub = null;

    if (this.overlayRef) {
      try {
        this.appRef.detachView(this.overlayRef.hostView);
      } catch { }
      this.overlayRef.destroy();
      this.overlayRef = null;
    }

    if (this.hostDiv) {
      try { this.hostDiv.remove(); } catch { }
    }
    this.hostDiv = null;
  }

  private applyData(): void {
    if (!this.overlayRef) return;
    const raw = this.latestData ?? this.weatherRoute();

    if (raw === null) {
      this.overlayRef.setInput('data', null);
      this.overlayRef.changeDetectorRef.detectChanges();
      this.latestData = null;

      if (this.hostDiv) this.hostDiv.style.display = '';
      if (this.tabEl) this.tabEl.style.display = this.visible ? 'none' : '';

      this.visible = this.createdRoute();
      this.visibleChange.emit();
      return;
    }

    if (raw === undefined || raw === null) return;

    this.overlayRef.setInput('data', raw);
    this.overlayRef.changeDetectorRef.detectChanges();
    this.latestData = null;
  }

  private applyVisibility(): void {
    const visible = this.visible;
    if (!this.hostDiv) {
      if (this.tabEl) this.tabEl.style.display = visible ? 'none' : '';
      return;
    }
    this.hostDiv.style.display = visible ? '' : 'none';
    if (this.tabEl) this.tabEl.style.display = visible ? 'none' : '';
  }

  private ensureTabInParent(targetParent: HTMLElement): void {
    if (!this.tabEl) {
      this.tabEl = this.renderer.createElement('button') as HTMLElement;
      this.tabEl.className = 'weather-tab-container weather-tab-container--hidden';
      const spanIcon = this.renderer.createElement('span');
      this.renderer.appendChild(spanIcon, this.renderer.createText('🌤'));
      this.renderer.appendChild(this.tabEl, spanIcon);

      this.tabEl.addEventListener('click', async () => {
        this.visible = true;
        const tp = this.resolveTargetParent() ?? targetParent;
        await this.recreateOverlayInParent(tp);
        this.applyVisibility();
        this.visibleChange.emit();
        this.schedulePosition(40);
      });
    }

    if (this.tabEl.parentElement !== targetParent) {
      try { this.tabEl.remove(); } catch { }
      targetParent.appendChild(this.tabEl);
    }

    this.tabEl.style.display = this.visible ? 'none' : '';
  }

  private removeTab(): void {
    if (this.tabEl) {
      try { this.tabEl.remove(); } catch { }
      this.tabEl = null;
    }
  }

  private schedulePosition(delay = 100): void {
    setTimeout(() => this.positionLeftOfFullscreenControl(), delay);
  }

  private positionLeftOfFullscreenControl(): void {
    const wrapper = this.hostDiv?.parentElement ?? this.mapDiv();
    if (!wrapper || !this.hostDiv) return;

    let fsEl: HTMLElement | null = null;
    for (const sel of this.fullscreenSelectors) {
      try { fsEl = wrapper.querySelector(sel) as HTMLElement | null; } catch { fsEl = null; }
      if (fsEl) break;
    }
    if (!fsEl) {
      for (const sel of this.fullscreenSelectors) {
        try { fsEl = document.querySelector(sel) as HTMLElement | null; } catch { fsEl = null; }
        if (fsEl) break;
      }
    }

    const fallbackRight = 64;
    if (!fsEl) {
      this.hostDiv.style.right = `${fallbackRight}px`;
      if (this.tabEl) this.tabEl.style.right = `${fallbackRight}px`;
      return;
    }

    const wrapperRect = wrapper.getBoundingClientRect();
    const fsRect = fsEl.getBoundingClientRect();
    const distanceFromRight = wrapperRect.right - fsRect.right;
    const gap = 8;
    const rightPx = Math.max(8, distanceFromRight + fsRect.width + gap);

    this.hostDiv.style.right = `${rightPx}px`;
    this.hostDiv.style.top = '12px';
    if (this.tabEl) {
      this.tabEl.style.position = 'absolute';
      this.tabEl.style.top = '12px';
      this.tabEl.style.right = `${rightPx}px`;
    }
  }

  private startListeners(): void {
    this.stopListeners();
    document.addEventListener('fullscreenchange', this.onFullScreenChangeHandler);
    (document as any).addEventListener?.('webkitfullscreenchange', this.onFullScreenChangeHandler);
    window.addEventListener('resize', this.onWindowResizeHandler);
  }

  private stopListeners(): void {
    document.removeEventListener('fullscreenchange', this.onFullScreenChangeHandler);
    (document as any).removeEventListener?.('webkitfullscreenchange', this.onFullScreenChangeHandler);
    window.removeEventListener('resize', this.onWindowResizeHandler);
  }

  private onFullScreenChangeHandler = async () => {
    const targetParent = this.resolveTargetParent();
    if (!targetParent) return;
    await this.recreateOverlayInParent(targetParent);
    this.ensureTabInParent(targetParent);
    this.schedulePosition(120);
  };

  private onWindowResizeHandler = () => this.schedulePosition(80);

  private waitForElementConnected(element: HTMLElement | null, timeout = 2000): Promise<boolean> {
    return new Promise(resolve => {
      if (!element) return resolve(false);
      if (element.isConnected) return resolve(true);

      let resolved = false;
      const to = setTimeout(() => {
        if (!resolved) {
          resolved = true;
          resolve(false);
          observer.disconnect();
        }
      }, timeout);

      const observer = new MutationObserver(() => {
        if (element.isConnected && !resolved) {
          resolved = true;
          clearTimeout(to);
          resolve(true);
          observer.disconnect();
        }
      });

      observer.observe(document.documentElement || document.body, { childList: true, subtree: true });
    });
  }
}