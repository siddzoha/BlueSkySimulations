import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IAircraft } from '../aircraft.model';
import { AircraftService } from '../service/aircraft.service';

import { AircraftFormService } from './aircraft-form.service';
import { AircraftUpdate } from './aircraft-update';

describe('Aircraft Management Update Component', () => {
  let comp: AircraftUpdate;
  let fixture: ComponentFixture<AircraftUpdate>;
  let activatedRoute: ActivatedRoute;
  let aircraftFormService: AircraftFormService;
  let aircraftService: AircraftService;

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

    fixture = TestBed.createComponent(AircraftUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    aircraftFormService = TestBed.inject(AircraftFormService);
    aircraftService = TestBed.inject(AircraftService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const aircraft: IAircraft = { id: 8931 };

      activatedRoute.data = of({ aircraft });
      comp.ngOnInit();

      expect(comp.aircraft).toEqual(aircraft);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAircraft>();
      const aircraft = { id: 14531 };
      vitest.spyOn(aircraftFormService, 'getAircraft').mockReturnValue(aircraft);
      vitest.spyOn(aircraftService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ aircraft });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(aircraft);
      saveSubject.complete();

      // THEN
      expect(aircraftFormService.getAircraft).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(aircraftService.update).toHaveBeenCalledWith(expect.objectContaining(aircraft));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAircraft>();
      const aircraft = { id: 14531 };
      vitest.spyOn(aircraftFormService, 'getAircraft').mockReturnValue({ id: null });
      vitest.spyOn(aircraftService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ aircraft: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(aircraft);
      saveSubject.complete();

      // THEN
      expect(aircraftFormService.getAircraft).toHaveBeenCalled();
      expect(aircraftService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAircraft>();
      const aircraft = { id: 14531 };
      vitest.spyOn(aircraftService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ aircraft });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(aircraftService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
