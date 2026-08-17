import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPilotProfile } from '../pilot-profile.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pilot-profile-detail',
  templateUrl: './pilot-profile-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class PilotProfileDetail {
  readonly pilotProfile = input<IPilotProfile | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
