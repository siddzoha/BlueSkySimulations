import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../pilot-profile.test-samples';

import { PilotProfileFormService } from './pilot-profile-form.service';

describe('PilotProfile Form Service', () => {
  let service: PilotProfileFormService;

  beforeEach(() => {
    service = TestBed.inject(PilotProfileFormService);
  });

  describe('Service methods', () => {
    describe('createPilotProfileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPilotProfileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            totalXp: expect.any(Object),
            rankTier: expect.any(Object),
            totalFlightHours: expect.any(Object),
            totalNightFlightHours: expect.any(Object),
            totalIfrFlightHours: expect.any(Object),
            flightsCompleted: expect.any(Object),
            user: expect.any(Object),
            achievementses: expect.any(Object),
          }),
        );
      });

      it('passing IPilotProfile should create a new form with FormGroup', () => {
        const formGroup = service.createPilotProfileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            totalXp: expect.any(Object),
            rankTier: expect.any(Object),
            totalFlightHours: expect.any(Object),
            totalNightFlightHours: expect.any(Object),
            totalIfrFlightHours: expect.any(Object),
            flightsCompleted: expect.any(Object),
            user: expect.any(Object),
            achievementses: expect.any(Object),
          }),
        );
      });
    });

    describe('getPilotProfile', () => {
      it('should return NewPilotProfile for default PilotProfile initial value', () => {
        const formGroup = service.createPilotProfileFormGroup(sampleWithNewData);

        const pilotProfile = service.getPilotProfile(formGroup);

        expect(pilotProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewPilotProfile for empty PilotProfile initial value', () => {
        const formGroup = service.createPilotProfileFormGroup();

        const pilotProfile = service.getPilotProfile(formGroup);

        expect(pilotProfile).toMatchObject({});
      });

      it('should return IPilotProfile', () => {
        const formGroup = service.createPilotProfileFormGroup(sampleWithRequiredData);

        const pilotProfile = service.getPilotProfile(formGroup);

        expect(pilotProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPilotProfile should not enable id FormControl', () => {
        const formGroup = service.createPilotProfileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPilotProfile should disable id FormControl', () => {
        const formGroup = service.createPilotProfileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
