import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WeatherOverlayComponent } from './weather-overlay.component';

describe('WeatherOverlayComponent', () => {
  let component: WeatherOverlayComponent;
  let fixture: ComponentFixture<WeatherOverlayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WeatherOverlayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WeatherOverlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
