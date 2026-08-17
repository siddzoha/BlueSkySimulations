import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { AircraftDetail } from './aircraft-detail';

describe('Aircraft Management Detail Component', () => {
  let comp: AircraftDetail;
  let fixture: ComponentFixture<AircraftDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./aircraft-detail').then(m => m.AircraftDetail),
              resolve: { aircraft: () => of({ id: 14531 }) },
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
    fixture = TestBed.createComponent(AircraftDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load aircraft on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AircraftDetail);

      // THEN
      expect(instance.aircraft()).toEqual(expect.objectContaining({ id: 14531 }));
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
