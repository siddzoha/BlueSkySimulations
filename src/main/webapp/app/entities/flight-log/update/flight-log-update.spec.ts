import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { AircraftService } from 'app/entities/aircraft/service/aircraft.service';
import { IAirport } from 'app/entities/airport/airport.model';
import { AirportService } from 'app/entities/airport/service/airport.service';
import { IPilotProfile } from 'app/entities/pilot-profile/pilot-profile.model';
import { PilotProfileService } from 'app/entities/pilot-profile/service/pilot-profile.service';
import { IFlightLog } from '../flight-log.model';
import { FlightLogService } from '../service/flight-log.service';

import { FlightLogFormService } from './flight-log-form.service';
import { FlightLogUpdate } from './flight-log-update';

describe('FlightLog Management Update Component', () => {
  let comp: FlightLogUpdate;
  let fixture: ComponentFixture<FlightLogUpdate>;
  let activatedRoute: ActivatedRoute;
  let flightLogFormService: FlightLogFormService;
  let flightLogService: FlightLogService;
  let aircraftService: AircraftService;
  let airportService: AirportService;
  let pilotProfileService: PilotProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(FlightLogUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    flightLogFormService = TestBed.inject(FlightLogFormService);
    flightLogService = TestBed.inject(FlightLogService);
    aircraftService = TestBed.inject(AircraftService);
    airportService = TestBed.inject(AirportService);
    pilotProfileService = TestBed.inject(PilotProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Aircraft query and add missing value', () => {
      const flightLog: IFlightLog = { id: 23292 };
      const aircraft: IAircraft = { id: 14531 };
      flightLog.aircraft = aircraft;

      const aircraftCollection: IAircraft[] = [{ id: 14531 }];
      vitest.spyOn(aircraftService, 'query').mockReturnValue(of(new HttpResponse({ body: aircraftCollection })));
      const additionalAircrafts = [aircraft];
      const expectedCollection: IAircraft[] = [...additionalAircrafts, ...aircraftCollection];
      vitest.spyOn(aircraftService, 'addAircraftToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ flightLog });
      comp.ngOnInit();

      expect(aircraftService.query).toHaveBeenCalled();
      expect(aircraftService.addAircraftToCollectionIfMissing).toHaveBeenCalledWith(
        aircraftCollection,
        ...additionalAircrafts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.aircraftsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Airport query and add missing value', () => {
      const flightLog: IFlightLog = { id: 23292 };
      const departureAirport: IAirport = { id: 18443 };
      flightLog.departureAirport = departureAirport;
      const arrivalAirport: IAirport = { id: 18443 };
      flightLog.arrivalAirport = arrivalAirport;

      const airportCollection: IAirport[] = [{ id: 18443 }];
      vitest.spyOn(airportService, 'query').mockReturnValue(of(new HttpResponse({ body: airportCollection })));
      const additionalAirports = [departureAirport, arrivalAirport];
      const expectedCollection: IAirport[] = [...additionalAirports, ...airportCollection];
      vitest.spyOn(airportService, 'addAirportToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ flightLog });
      comp.ngOnInit();

      expect(airportService.query).toHaveBeenCalled();
      expect(airportService.addAirportToCollectionIfMissing).toHaveBeenCalledWith(
        airportCollection,
        ...additionalAirports.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.airportsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call PilotProfile query and add missing value', () => {
      const flightLog: IFlightLog = { id: 23292 };
      const pilot: IPilotProfile = { id: 18599 };
      flightLog.pilot = pilot;

      const pilotProfileCollection: IPilotProfile[] = [{ id: 18599 }];
      vitest.spyOn(pilotProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: pilotProfileCollection })));
      const additionalPilotProfiles = [pilot];
      const expectedCollection: IPilotProfile[] = [...additionalPilotProfiles, ...pilotProfileCollection];
      vitest.spyOn(pilotProfileService, 'addPilotProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ flightLog });
      comp.ngOnInit();

      expect(pilotProfileService.query).toHaveBeenCalled();
      expect(pilotProfileService.addPilotProfileToCollectionIfMissing).toHaveBeenCalledWith(
        pilotProfileCollection,
        ...additionalPilotProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.pilotProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const flightLog: IFlightLog = { id: 23292 };
      const aircraft: IAircraft = { id: 14531 };
      flightLog.aircraft = aircraft;
      const departureAirport: IAirport = { id: 18443 };
      flightLog.departureAirport = departureAirport;
      const arrivalAirport: IAirport = { id: 18443 };
      flightLog.arrivalAirport = arrivalAirport;
      const pilot: IPilotProfile = { id: 18599 };
      flightLog.pilot = pilot;

      activatedRoute.data = of({ flightLog });
      comp.ngOnInit();

      expect(comp.aircraftsSharedCollection()).toContainEqual(aircraft);
      expect(comp.airportsSharedCollection()).toContainEqual(departureAirport);
      expect(comp.airportsSharedCollection()).toContainEqual(arrivalAirport);
      expect(comp.pilotProfilesSharedCollection()).toContainEqual(pilot);
      expect(comp.flightLog).toEqual(flightLog);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFlightLog>();
      const flightLog = { id: 15441 };
      vitest.spyOn(flightLogFormService, 'getFlightLog').mockReturnValue(flightLog);
      vitest.spyOn(flightLogService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ flightLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(flightLog);
      saveSubject.complete();

      // THEN
      expect(flightLogFormService.getFlightLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(flightLogService.update).toHaveBeenCalledWith(expect.objectContaining(flightLog));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFlightLog>();
      const flightLog = { id: 15441 };
      vitest.spyOn(flightLogFormService, 'getFlightLog').mockReturnValue({ id: null });
      vitest.spyOn(flightLogService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ flightLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(flightLog);
      saveSubject.complete();

      // THEN
      expect(flightLogFormService.getFlightLog).toHaveBeenCalled();
      expect(flightLogService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IFlightLog>();
      const flightLog = { id: 15441 };
      vitest.spyOn(flightLogService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ flightLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(flightLogService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAircraft', () => {
      it('should forward to aircraftService', () => {
        const entity = { id: 14531 };
        const entity2 = { id: 8931 };
        vitest.spyOn(aircraftService, 'compareAircraft');
        comp.compareAircraft(entity, entity2);
        expect(aircraftService.compareAircraft).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareAirport', () => {
      it('should forward to airportService', () => {
        const entity = { id: 18443 };
        const entity2 = { id: 14999 };
        vitest.spyOn(airportService, 'compareAirport');
        comp.compareAirport(entity, entity2);
        expect(airportService.compareAirport).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePilotProfile', () => {
      it('should forward to pilotProfileService', () => {
        const entity = { id: 18599 };
        const entity2 = { id: 16800 };
        vitest.spyOn(pilotProfileService, 'comparePilotProfile');
        comp.comparePilotProfile(entity, entity2);
        expect(pilotProfileService.comparePilotProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
