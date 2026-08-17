import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IAirport } from '../airport.model';
import { AirportService } from '../service/airport.service';

import { AirportFormService } from './airport-form.service';
import { AirportUpdate } from './airport-update';

describe('Airport Management Update Component', () => {
  let comp: AirportUpdate;
  let fixture: ComponentFixture<AirportUpdate>;
  let activatedRoute: ActivatedRoute;
  let airportFormService: AirportFormService;
  let airportService: AirportService;

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

    fixture = TestBed.createComponent(AirportUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    airportFormService = TestBed.inject(AirportFormService);
    airportService = TestBed.inject(AirportService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const airport: IAirport = { id: 14999 };

      activatedRoute.data = of({ airport });
      comp.ngOnInit();

      expect(comp.airport).toEqual(airport);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAirport>();
      const airport = { id: 18443 };
      vitest.spyOn(airportFormService, 'getAirport').mockReturnValue(airport);
      vitest.spyOn(airportService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ airport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(airport);
      saveSubject.complete();

      // THEN
      expect(airportFormService.getAirport).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(airportService.update).toHaveBeenCalledWith(expect.objectContaining(airport));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAirport>();
      const airport = { id: 18443 };
      vitest.spyOn(airportFormService, 'getAirport').mockReturnValue({ id: null });
      vitest.spyOn(airportService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ airport: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(airport);
      saveSubject.complete();

      // THEN
      expect(airportFormService.getAirport).toHaveBeenCalled();
      expect(airportService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAirport>();
      const airport = { id: 18443 };
      vitest.spyOn(airportService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ airport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(airportService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
