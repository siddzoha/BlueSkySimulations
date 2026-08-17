import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlightDispatcher } from './flight-dispatcher';

describe('FlightDispatcher', () => {
  let component: FlightDispatcher;
  let fixture: ComponentFixture<FlightDispatcher>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightDispatcher],
    }).compileComponents();

    fixture = TestBed.createComponent(FlightDispatcher);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
