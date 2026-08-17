import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IPilotProfile } from 'app/entities/pilot-profile/pilot-profile.model';
import { PilotProfileService } from 'app/entities/pilot-profile/service/pilot-profile.service';
import { IAchievement } from '../achievement.model';
import { AchievementService } from '../service/achievement.service';

import { AchievementFormService } from './achievement-form.service';
import { AchievementUpdate } from './achievement-update';

describe('Achievement Management Update Component', () => {
  let comp: AchievementUpdate;
  let fixture: ComponentFixture<AchievementUpdate>;
  let activatedRoute: ActivatedRoute;
  let achievementFormService: AchievementFormService;
  let achievementService: AchievementService;
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

    fixture = TestBed.createComponent(AchievementUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    achievementFormService = TestBed.inject(AchievementFormService);
    achievementService = TestBed.inject(AchievementService);
    pilotProfileService = TestBed.inject(PilotProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PilotProfile query and add missing value', () => {
      const achievement: IAchievement = { id: 27064 };
      const pilotses: IPilotProfile[] = [{ id: 18599 }];
      achievement.pilotses = pilotses;

      const pilotProfileCollection: IPilotProfile[] = [{ id: 18599 }];
      vitest.spyOn(pilotProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: pilotProfileCollection })));
      const additionalPilotProfiles = [...pilotses];
      const expectedCollection: IPilotProfile[] = [...additionalPilotProfiles, ...pilotProfileCollection];
      vitest.spyOn(pilotProfileService, 'addPilotProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ achievement });
      comp.ngOnInit();

      expect(pilotProfileService.query).toHaveBeenCalled();
      expect(pilotProfileService.addPilotProfileToCollectionIfMissing).toHaveBeenCalledWith(
        pilotProfileCollection,
        ...additionalPilotProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.pilotProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const achievement: IAchievement = { id: 27064 };
      const pilots: IPilotProfile = { id: 18599 };
      achievement.pilotses = [pilots];

      activatedRoute.data = of({ achievement });
      comp.ngOnInit();

      expect(comp.pilotProfilesSharedCollection()).toContainEqual(pilots);
      expect(comp.achievement).toEqual(achievement);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAchievement>();
      const achievement = { id: 17559 };
      vitest.spyOn(achievementFormService, 'getAchievement').mockReturnValue(achievement);
      vitest.spyOn(achievementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ achievement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(achievement);
      saveSubject.complete();

      // THEN
      expect(achievementFormService.getAchievement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(achievementService.update).toHaveBeenCalledWith(expect.objectContaining(achievement));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAchievement>();
      const achievement = { id: 17559 };
      vitest.spyOn(achievementFormService, 'getAchievement').mockReturnValue({ id: null });
      vitest.spyOn(achievementService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ achievement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(achievement);
      saveSubject.complete();

      // THEN
      expect(achievementFormService.getAchievement).toHaveBeenCalled();
      expect(achievementService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAchievement>();
      const achievement = { id: 17559 };
      vitest.spyOn(achievementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ achievement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(achievementService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
