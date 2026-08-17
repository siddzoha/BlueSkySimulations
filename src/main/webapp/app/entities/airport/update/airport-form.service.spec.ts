import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../airport.test-samples';

import { AirportFormService } from './airport-form.service';

describe('Airport Form Service', () => {
  let service: AirportFormService;

  beforeEach(() => {
    service = TestBed.inject(AirportFormService);
  });

  describe('Service methods', () => {
    describe('createAirportFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAirportFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            icaoCode: expect.any(Object),
            iataCode: expect.any(Object),
            name: expect.any(Object),
            city: expect.any(Object),
            country: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            elevationFt: expect.any(Object),
            longestRunwayFt: expect.any(Object),
          }),
        );
      });

      it('passing IAirport should create a new form with FormGroup', () => {
        const formGroup = service.createAirportFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            icaoCode: expect.any(Object),
            iataCode: expect.any(Object),
            name: expect.any(Object),
            city: expect.any(Object),
            country: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            elevationFt: expect.any(Object),
            longestRunwayFt: expect.any(Object),
          }),
        );
      });
    });

    describe('getAirport', () => {
      it('should return NewAirport for default Airport initial value', () => {
        const formGroup = service.createAirportFormGroup(sampleWithNewData);

        const airport = service.getAirport(formGroup);

        expect(airport).toMatchObject(sampleWithNewData);
      });

      it('should return NewAirport for empty Airport initial value', () => {
        const formGroup = service.createAirportFormGroup();

        const airport = service.getAirport(formGroup);

        expect(airport).toMatchObject({});
      });

      it('should return IAirport', () => {
        const formGroup = service.createAirportFormGroup(sampleWithRequiredData);

        const airport = service.getAirport(formGroup);

        expect(airport).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAirport should not enable id FormControl', () => {
        const formGroup = service.createAirportFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAirport should disable id FormControl', () => {
        const formGroup = service.createAirportFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
