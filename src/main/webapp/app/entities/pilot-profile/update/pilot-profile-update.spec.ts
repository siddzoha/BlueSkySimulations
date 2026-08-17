import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IAchievement } from 'app/entities/achievement/achievement.model';
import { AchievementService } from 'app/entities/achievement/service/achievement.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IPilotProfile } from '../pilot-profile.model';
import { PilotProfileService } from '../service/pilot-profile.service';

import { PilotProfileFormService } from './pilot-profile-form.service';
import { PilotProfileUpdate } from './pilot-profile-update';

describe('PilotProfile Management Update Component', () => {
  let comp: PilotProfileUpdate;
  let fixture: ComponentFixture<PilotProfileUpdate>;
  let activatedRoute: ActivatedRoute;
  let pilotProfileFormService: PilotProfileFormService;
  let pilotProfileService: PilotProfileService;
  let userService: UserService;
  let achievementService: AchievementService;

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

    fixture = TestBed.createComponent(PilotProfileUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pilotProfileFormService = TestBed.inject(PilotProfileFormService);
    pilotProfileService = TestBed.inject(PilotProfileService);
    userService = TestBed.inject(UserService);
    achievementService = TestBed.inject(AchievementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const pilotProfile: IPilotProfile = { id: 16800 };
      const user: IUser = { id: 3944 };
      pilotProfile.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pilotProfile });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Achievement query and add missing value', () => {
      const pilotProfile: IPilotProfile = { id: 16800 };
      const achievementses: IAchievement[] = [{ id: 17559 }];
      pilotProfile.achievementses = achievementses;

      const achievementCollection: IAchievement[] = [{ id: 17559 }];
      vitest.spyOn(achievementService, 'query').mockReturnValue(of(new HttpResponse({ body: achievementCollection })));
      const additionalAchievements = [...achievementses];
      const expectedCollection: IAchievement[] = [...additionalAchievements, ...achievementCollection];
      vitest.spyOn(achievementService, 'addAchievementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pilotProfile });
      comp.ngOnInit();

      expect(achievementService.query).toHaveBeenCalled();
      expect(achievementService.addAchievementToCollectionIfMissing).toHaveBeenCalledWith(
        achievementCollection,
        ...additionalAchievements.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.achievementsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const pilotProfile: IPilotProfile = { id: 16800 };
      const user: IUser = { id: 3944 };
      pilotProfile.user = user;
      const achievements: IAchievement = { id: 17559 };
      pilotProfile.achievementses = [achievements];

      activatedRoute.data = of({ pilotProfile });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.achievementsSharedCollection()).toContainEqual(achievements);
      expect(comp.pilotProfile).toEqual(pilotProfile);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPilotProfile>();
      const pilotProfile = { id: 18599 };
      vitest.spyOn(pilotProfileFormService, 'getPilotProfile').mockReturnValue(pilotProfile);
      vitest.spyOn(pilotProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pilotProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pilotProfile);
      saveSubject.complete();

      // THEN
      expect(pilotProfileFormService.getPilotProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pilotProfileService.update).toHaveBeenCalledWith(expect.objectContaining(pilotProfile));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPilotProfile>();
      const pilotProfile = { id: 18599 };
      vitest.spyOn(pilotProfileFormService, 'getPilotProfile').mockReturnValue({ id: null });
      vitest.spyOn(pilotProfileService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pilotProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pilotProfile);
      saveSubject.complete();

      // THEN
      expect(pilotProfileFormService.getPilotProfile).toHaveBeenCalled();
      expect(pilotProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPilotProfile>();
      const pilotProfile = { id: 18599 };
      vitest.spyOn(pilotProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pilotProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pilotProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareAchievement', () => {
      it('should forward to achievementService', () => {
        const entity = { id: 17559 };
        const entity2 = { id: 27064 };
        vitest.spyOn(achievementService, 'compareAchievement');
        comp.compareAchievement(entity, entity2);
        expect(achievementService.compareAchievement).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
