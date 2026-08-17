import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { FlightLogDetail } from './flight-log-detail';

describe('FlightLog Management Detail Component', () => {
  let comp: FlightLogDetail;
  let fixture: ComponentFixture<FlightLogDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./flight-log-detail').then(m => m.FlightLogDetail),
              resolve: { flightLog: () => of({ id: 15441 }) },
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
    fixture = TestBed.createComponent(FlightLogDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load flightLog on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FlightLogDetail);

      // THEN
      expect(instance.flightLog()).toEqual(expect.objectContaining({ id: 15441 }));
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
