import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPilotProfile } from '../pilot-profile.model';
import { PilotProfileService } from '../service/pilot-profile.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './pilot-profile-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class PilotProfileDeleteDialog {
  pilotProfile?: IPilotProfile;

  protected readonly pilotProfileService = inject(PilotProfileService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pilotProfileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
