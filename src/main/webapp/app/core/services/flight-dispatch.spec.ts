import { TestBed } from '@angular/core/testing';

import { FlightDispatch } from './flight-dispatch';

describe('FlightDispatch', () => {
  let service: FlightDispatch;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlightDispatch);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
