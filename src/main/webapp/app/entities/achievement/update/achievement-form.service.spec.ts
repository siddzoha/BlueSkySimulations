import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../achievement.test-samples';

import { AchievementFormService } from './achievement-form.service';

describe('Achievement Form Service', () => {
  let service: AchievementFormService;

  beforeEach(() => {
    service = TestBed.inject(AchievementFormService);
  });

  describe('Service methods', () => {
    describe('createAchievementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAchievementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            badgeIcon: expect.any(Object),
            xpReward: expect.any(Object),
            pilotses: expect.any(Object),
          }),
        );
      });

      it('passing IAchievement should create a new form with FormGroup', () => {
        const formGroup = service.createAchievementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            badgeIcon: expect.any(Object),
            xpReward: expect.any(Object),
            pilotses: expect.any(Object),
          }),
        );
      });
    });

    describe('getAchievement', () => {
      it('should return NewAchievement for default Achievement initial value', () => {
        const formGroup = service.createAchievementFormGroup(sampleWithNewData);

        const achievement = service.getAchievement(formGroup);

        expect(achievement).toMatchObject(sampleWithNewData);
      });

      it('should return NewAchievement for empty Achievement initial value', () => {
        const formGroup = service.createAchievementFormGroup();

        const achievement = service.getAchievement(formGroup);

        expect(achievement).toMatchObject({});
      });

      it('should return IAchievement', () => {
        const formGroup = service.createAchievementFormGroup(sampleWithRequiredData);

        const achievement = service.getAchievement(formGroup);

        expect(achievement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAchievement should not enable id FormControl', () => {
        const formGroup = service.createAchievementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAchievement should disable id FormControl', () => {
        const formGroup = service.createAchievementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
