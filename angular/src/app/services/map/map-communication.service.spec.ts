import { TestBed } from '@angular/core/testing';

import { MapCommunicationService } from './map-communication.service';

describe('MapCommunicationService', () => {
  let service: MapCommunicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MapCommunicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
