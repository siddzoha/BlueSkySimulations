import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IAchievement } from '../achievement.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-achievement-detail',
  templateUrl: './achievement-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class AchievementDetail {
  readonly achievement = input<IAchievement | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
