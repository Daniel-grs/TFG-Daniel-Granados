import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WeatherOverlayHostComponent } from './weather-overlay-host.component';

describe('WeatherOverlayHostComponent', () => {
  let component: WeatherOverlayHostComponent;
  let fixture: ComponentFixture<WeatherOverlayHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WeatherOverlayHostComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WeatherOverlayHostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
