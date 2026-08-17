import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IAirport } from '../airport.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-airport-detail',
  templateUrl: './airport-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class AirportDetail {
  readonly airport = input<IAirport | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
