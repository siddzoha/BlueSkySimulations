import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../aircraft.test-samples';

import { AircraftFormService } from './aircraft-form.service';

describe('Aircraft Form Service', () => {
  let service: AircraftFormService;

  beforeEach(() => {
    service = TestBed.inject(AircraftFormService);
  });

  describe('Service methods', () => {
    describe('createAircraftFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAircraftFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tailNumber: expect.any(Object),
            modelName: expect.any(Object),
            icaoType: expect.any(Object),
            category: expect.any(Object),
            cruiseSpeedKnots: expect.any(Object),
            maxRangeNm: expect.any(Object),
            serviceCeilingFt: expect.any(Object),
            fuelBurnGph: expect.any(Object),
            minRunwayLengthFt: expect.any(Object),
          }),
        );
      });

      it('passing IAircraft should create a new form with FormGroup', () => {
        const formGroup = service.createAircraftFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tailNumber: expect.any(Object),
            modelName: expect.any(Object),
            icaoType: expect.any(Object),
            category: expect.any(Object),
            cruiseSpeedKnots: expect.any(Object),
            maxRangeNm: expect.any(Object),
            serviceCeilingFt: expect.any(Object),
            fuelBurnGph: expect.any(Object),
            minRunwayLengthFt: expect.any(Object),
          }),
        );
      });
    });

    describe('getAircraft', () => {
      it('should return NewAircraft for default Aircraft initial value', () => {
        const formGroup = service.createAircraftFormGroup(sampleWithNewData);

        const aircraft = service.getAircraft(formGroup);

        expect(aircraft).toMatchObject(sampleWithNewData);
      });

      it('should return NewAircraft for empty Aircraft initial value', () => {
        const formGroup = service.createAircraftFormGroup();

        const aircraft = service.getAircraft(formGroup);

        expect(aircraft).toMatchObject({});
      });

      it('should return IAircraft', () => {
        const formGroup = service.createAircraftFormGroup(sampleWithRequiredData);

        const aircraft = service.getAircraft(formGroup);

        expect(aircraft).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAircraft should not enable id FormControl', () => {
        const formGroup = service.createAircraftFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAircraft should disable id FormControl', () => {
        const formGroup = service.createAircraftFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
