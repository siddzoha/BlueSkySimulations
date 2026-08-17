import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../flight-log.test-samples';

import { FlightLogFormService } from './flight-log-form.service';

describe('FlightLog Form Service', () => {
  let service: FlightLogFormService;

  beforeEach(() => {
    service = TestBed.inject(FlightLogFormService);
  });

  describe('Service methods', () => {
    describe('createFlightLogFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFlightLogFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            departureTime: expect.any(Object),
            arrivalTime: expect.any(Object),
            durationHours: expect.any(Object),
            distanceNm: expect.any(Object),
            cruiseAltitudeFt: expect.any(Object),
            fuelUsedGallons: expect.any(Object),
            xpEarned: expect.any(Object),
            flightRules: expect.any(Object),
            remarks: expect.any(Object),
            aircraft: expect.any(Object),
            departureAirport: expect.any(Object),
            arrivalAirport: expect.any(Object),
            pilot: expect.any(Object),
          }),
        );
      });

      it('passing IFlightLog should create a new form with FormGroup', () => {
        const formGroup = service.createFlightLogFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            departureTime: expect.any(Object),
            arrivalTime: expect.any(Object),
            durationHours: expect.any(Object),
            distanceNm: expect.any(Object),
            cruiseAltitudeFt: expect.any(Object),
            fuelUsedGallons: expect.any(Object),
            xpEarned: expect.any(Object),
            flightRules: expect.any(Object),
            remarks: expect.any(Object),
            aircraft: expect.any(Object),
            departureAirport: expect.any(Object),
            arrivalAirport: expect.any(Object),
            pilot: expect.any(Object),
          }),
        );
      });
    });

    describe('getFlightLog', () => {
      it('should return NewFlightLog for default FlightLog initial value', () => {
        const formGroup = service.createFlightLogFormGroup(sampleWithNewData);

        const flightLog = service.getFlightLog(formGroup);

        expect(flightLog).toMatchObject(sampleWithNewData);
      });

      it('should return NewFlightLog for empty FlightLog initial value', () => {
        const formGroup = service.createFlightLogFormGroup();

        const flightLog = service.getFlightLog(formGroup);

        expect(flightLog).toMatchObject({});
      });

      it('should return IFlightLog', () => {
        const formGroup = service.createFlightLogFormGroup(sampleWithRequiredData);

        const flightLog = service.getFlightLog(formGroup);

        expect(flightLog).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFlightLog should not enable id FormControl', () => {
        const formGroup = service.createFlightLogFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFlightLog should disable id FormControl', () => {
        const formGroup = service.createFlightLogFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
