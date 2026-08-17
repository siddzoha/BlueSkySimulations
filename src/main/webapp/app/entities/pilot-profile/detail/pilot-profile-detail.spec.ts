import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { PilotProfileDetail } from './pilot-profile-detail';

describe('PilotProfile Management Detail Component', () => {
  let comp: PilotProfileDetail;
  let fixture: ComponentFixture<PilotProfileDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./pilot-profile-detail').then(m => m.PilotProfileDetail),
              resolve: { pilotProfile: () => of({ id: 18599 }) },
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
    fixture = TestBed.createComponent(PilotProfileDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load pilotProfile on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PilotProfileDetail);

      // THEN
      expect(instance.pilotProfile()).toEqual(expect.objectContaining({ id: 18599 }));
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
