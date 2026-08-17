import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { AchievementDetail } from './achievement-detail';

describe('Achievement Management Detail Component', () => {
  let comp: AchievementDetail;
  let fixture: ComponentFixture<AchievementDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./achievement-detail').then(m => m.AchievementDetail),
              resolve: { achievement: () => of({ id: 17559 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AchievementDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load achievement on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AchievementDetail);

      // THEN
      expect(instance.achievement()).toEqual(expect.objectContaining({ id: 17559 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
