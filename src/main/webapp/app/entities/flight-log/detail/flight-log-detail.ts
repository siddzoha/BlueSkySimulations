import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IFlightLog } from '../flight-log.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-flight-log-detail',
  templateUrl: './flight-log-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class FlightLogDetail {
  readonly flightLog = input<IFlightLog | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
