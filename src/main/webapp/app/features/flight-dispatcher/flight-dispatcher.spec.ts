import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

import { FlightDispatcher } from './flight-dispatcher';
import { AirportService } from '../../entities/airport/service/airport.service';
import { AircraftService } from '../../entities/aircraft/service/aircraft.service';
import { FlightDispatch } from '../../core/services/flight-dispatch';
import { IAirport } from '../../entities/airport/airport.model';
import { IAircraft } from '../../entities/aircraft/aircraft.model';
import { RoutePlan } from '../../core/models/route-plan';

vi.mock('leaflet', () => ({
  map: () => ({
    setView: vi.fn().mockReturnThis(),
    fitBounds: vi.fn().mockReturnThis(),
    panTo: vi.fn().mockReturnThis(),
    remove: vi.fn(),
  }),
  tileLayer: () => ({
    addTo: vi.fn().mockReturnThis(),
  }),
  control: {
    layers: () => ({
      addTo: vi.fn().mockReturnThis(),
    }),
  },
  layerGroup: () => ({
    addTo: vi.fn().mockReturnThis(),
    clearLayers: vi.fn().mockReturnThis(),
    addLayer: vi.fn().mockReturnThis(),
  }),
  divIcon: () => ({}),
  marker: () => ({
    bindPopup: vi.fn().mockReturnThis(),
    on: vi.fn().mockReturnThis(),
    setIcon: vi.fn().mockReturnThis(),
    setZIndexOffset: vi.fn().mockReturnThis(),
    closePopup: vi.fn(),
  }),
  circleMarker: () => ({
    bindPopup: vi.fn().mockReturnThis(),
  }),
  polyline: () => ({
    bindTooltip: vi.fn().mockReturnThis(),
  }),
  latLngBounds: () => ({}),
}));

describe('FlightDispatcher', () => {
  let component: FlightDispatcher;
  let fixture: ComponentFixture<FlightDispatcher>;
  let flightDispatchService: FlightDispatch;

  const mockAirports: IAirport[] = [
    {
      id: 1,
      icaoCode: 'CYYZ',
      name: 'Toronto Pearson',
      city: 'Toronto',
      country: 'Canada',
      latitude: 43.6777,
      longitude: -79.6248,
      elevationFt: 569,
      longestRunwayFt: 11120,
    },
    {
      id: 2,
      icaoCode: 'CYUL',
      name: 'Montreal Trudeau',
      city: 'Montreal',
      country: 'Canada',
      latitude: 45.4786,
      longitude: -73.7408,
      elevationFt: 118,
      longestRunwayFt: 11000,
    },
  ];

  const mockAircraft: IAircraft[] = [
    {
      id: 10,
      modelName: 'Boeing 737-800',
      icaoType: 'B738',
      serviceCeilingFt: 41000,
      cruiseSpeedKnots: 450,
      fuelBurnGph: 850,
    },
  ];

  const mockRoutePlan: RoutePlan = {
    departureAirport: mockAirports[0],
    arrivalAirport: mockAirports[1],
    aircraft: mockAircraft[0],
    distanceNm: 272.5,
    initHeading: 65,
    estFlightTimeHours: 0.85,
    estFuelGallons: 750,
    xpReward: 458,
    targetCruiseAltitudeFt: 27000,
    climbRateFpm: 2400,
    descentRateFpm: -1800,
    takeoffWeightLbs: 145000,
    costIndex: 30,
    profileWaypoints: [
      {
        latitude: 43.6777,
        longitude: -79.6248,
        altitudeFt: 569,
        distanceFromOriginNm: 0,
        verticalSpeedFpm: 2400,
        flightPhase: 'CLIMB',
      },
      {
        latitude: 45.4786,
        longitude: -73.7408,
        altitudeFt: 118,
        distanceFromOriginNm: 272.5,
        verticalSpeedFpm: -1800,
        flightPhase: 'DESCENT',
      },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightDispatcher],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    const airportService = TestBed.inject(AirportService);
    const aircraftService = TestBed.inject(AircraftService);
    flightDispatchService = TestBed.inject(FlightDispatch);

    vi.spyOn(airportService, 'query').mockReturnValue(of(new HttpResponse({ body: mockAirports })));
    vi.spyOn(aircraftService, 'query').mockReturnValue(of(new HttpResponse({ body: mockAircraft })));
    vi.spyOn(flightDispatchService, 'calculateRoute').mockReturnValue(of(mockRoutePlan));
    vi.spyOn(flightDispatchService, 'getAircraftWeightLimits').mockReturnValue(
      of({ emptyWeightLbs: 90000, maxTakeoffWeightLbs: 174000, recommendedTakeoffWeightLbs: 142680 }),
    );

    fixture = TestBed.createComponent(FlightDispatcher);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create and load airports and aircraft on init', () => {
    expect(component).toBeTruthy();
    expect(component.airports.length).toBe(2);
    expect(component.aircraftList.length).toBe(1);
  });

  it('should auto-swap airports when selecting conflicting endpoints from map', () => {
    component.selectAirportFromMap(1, 'DEP');
    expect(component.selectedDepartureAirportId).toBe(1);

    component.selectAirportFromMap(2, 'ARR');
    expect(component.selectedArrivalAirportId).toBe(2);

    component.selectAirportFromMap(1, 'ARR');
    expect(component.selectedArrivalAirportId).toBe(1);
    expect(component.selectedDepartureAirportId).toBe(2);
  });

  it('should load weight limits and set recommended weight when aircraft is selected', () => {
    component.selectedAircraftId = 10;
    component.onAircraftChange();

    expect(component.weightLimits).toBeTruthy();
    expect(component.weightLimits?.maxTakeoffWeightLbs).toBe(174000);
    expect(component.takeoffWeightLbs).toBe(142680);
  });

  it('should calculate route with weight and cost index parameters', () => {
    component.selectedDepartureAirportId = 1;
    component.selectedArrivalAirportId = 2;
    component.selectedAircraftId = 10;
    component.takeoffWeightLbs = 145000;
    component.costIndex = 35;

    component.onCalculateRoute();

    expect(flightDispatchService.calculateRoute).toHaveBeenCalledWith(1, 2, 10, 145000, 35);
    expect(component.routePlan).toEqual(mockRoutePlan);
  });
});
