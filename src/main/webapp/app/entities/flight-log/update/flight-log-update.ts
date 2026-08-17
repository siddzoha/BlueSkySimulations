import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { AircraftService } from 'app/entities/aircraft/service/aircraft.service';
import { IAirport } from 'app/entities/airport/airport.model';
import { AirportService } from 'app/entities/airport/service/airport.service';
import { FlightRules } from 'app/entities/enumerations/flight-rules.model';
import { IPilotProfile } from 'app/entities/pilot-profile/pilot-profile.model';
import { PilotProfileService } from 'app/entities/pilot-profile/service/pilot-profile.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IFlightLog } from '../flight-log.model';
import { FlightLogService } from '../service/flight-log.service';

import { FlightLogFormGroup, FlightLogFormService } from './flight-log-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-flight-log-update',
  templateUrl: './flight-log-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class FlightLogUpdate implements OnInit {
  readonly isSaving = signal(false);
  flightLog: IFlightLog | null = null;
  flightRulesValues = Object.keys(FlightRules);

  aircraftsSharedCollection = signal<IAircraft[]>([]);
  airportsSharedCollection = signal<IAirport[]>([]);
  pilotProfilesSharedCollection = signal<IPilotProfile[]>([]);

  protected flightLogService = inject(FlightLogService);
  protected flightLogFormService = inject(FlightLogFormService);
  protected aircraftService = inject(AircraftService);
  protected airportService = inject(AirportService);
  protected pilotProfileService = inject(PilotProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FlightLogFormGroup = this.flightLogFormService.createFlightLogFormGroup();

  compareAircraft = (o1: IAircraft | null, o2: IAircraft | null): boolean => this.aircraftService.compareAircraft(o1, o2);

  compareAirport = (o1: IAirport | null, o2: IAirport | null): boolean => this.airportService.compareAirport(o1, o2);

  comparePilotProfile = (o1: IPilotProfile | null, o2: IPilotProfile | null): boolean =>
    this.pilotProfileService.comparePilotProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ flightLog }) => {
      this.flightLog = flightLog;
      if (flightLog) {
        this.updateForm(flightLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const flightLog = this.flightLogFormService.getFlightLog(this.editForm);
    if (flightLog.id === null) {
      this.subscribeToSaveResponse(this.flightLogService.create(flightLog));
    } else {
      this.subscribeToSaveResponse(this.flightLogService.update(flightLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IFlightLog | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(flightLog: IFlightLog): void {
    this.flightLog = flightLog;
    this.flightLogFormService.resetForm(this.editForm, flightLog);

    this.aircraftsSharedCollection.update(aircrafts =>
      this.aircraftService.addAircraftToCollectionIfMissing<IAircraft>(aircrafts, flightLog.aircraft),
    );
    this.airportsSharedCollection.update(airports =>
      this.airportService.addAirportToCollectionIfMissing<IAirport>(airports, flightLog.departureAirport, flightLog.arrivalAirport),
    );
    this.pilotProfilesSharedCollection.update(pilotProfiles =>
      this.pilotProfileService.addPilotProfileToCollectionIfMissing<IPilotProfile>(pilotProfiles, flightLog.pilot),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.aircraftService
      .query()
      .pipe(map((res: HttpResponse<IAircraft[]>) => res.body ?? []))
      .pipe(
        map((aircrafts: IAircraft[]) =>
          this.aircraftService.addAircraftToCollectionIfMissing<IAircraft>(aircrafts, this.flightLog?.aircraft),
        ),
      )
      .subscribe((aircrafts: IAircraft[]) => this.aircraftsSharedCollection.set(aircrafts));

    this.airportService
      .query()
      .pipe(map((res: HttpResponse<IAirport[]>) => res.body ?? []))
      .pipe(
        map((airports: IAirport[]) =>
          this.airportService.addAirportToCollectionIfMissing<IAirport>(
            airports,
            this.flightLog?.departureAirport,
            this.flightLog?.arrivalAirport,
          ),
        ),
      )
      .subscribe((airports: IAirport[]) => this.airportsSharedCollection.set(airports));

    this.pilotProfileService
      .query()
      .pipe(map((res: HttpResponse<IPilotProfile[]>) => res.body ?? []))
      .pipe(
        map((pilotProfiles: IPilotProfile[]) =>
          this.pilotProfileService.addPilotProfileToCollectionIfMissing<IPilotProfile>(pilotProfiles, this.flightLog?.pilot),
        ),
      )
      .subscribe((pilotProfiles: IPilotProfile[]) => this.pilotProfilesSharedCollection.set(pilotProfiles));
  }
}
