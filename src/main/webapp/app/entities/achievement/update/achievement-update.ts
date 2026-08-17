import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IPilotProfile } from 'app/entities/pilot-profile/pilot-profile.model';
import { PilotProfileService } from 'app/entities/pilot-profile/service/pilot-profile.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IAchievement } from '../achievement.model';
import { AchievementService } from '../service/achievement.service';

import { AchievementFormGroup, AchievementFormService } from './achievement-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-achievement-update',
  templateUrl: './achievement-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AchievementUpdate implements OnInit {
  readonly isSaving = signal(false);
  achievement: IAchievement | null = null;

  pilotProfilesSharedCollection = signal<IPilotProfile[]>([]);

  protected achievementService = inject(AchievementService);
  protected achievementFormService = inject(AchievementFormService);
  protected pilotProfileService = inject(PilotProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AchievementFormGroup = this.achievementFormService.createAchievementFormGroup();

  comparePilotProfile = (o1: IPilotProfile | null, o2: IPilotProfile | null): boolean =>
    this.pilotProfileService.comparePilotProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ achievement }) => {
      this.achievement = achievement;
      if (achievement) {
        this.updateForm(achievement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const achievement = this.achievementFormService.getAchievement(this.editForm);
    if (achievement.id === null) {
      this.subscribeToSaveResponse(this.achievementService.create(achievement));
    } else {
      this.subscribeToSaveResponse(this.achievementService.update(achievement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAchievement | null>): void {
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

  protected updateForm(achievement: IAchievement): void {
    this.achievement = achievement;
    this.achievementFormService.resetForm(this.editForm, achievement);

    this.pilotProfilesSharedCollection.update(pilotProfiles =>
      this.pilotProfileService.addPilotProfileToCollectionIfMissing<IPilotProfile>(pilotProfiles, ...(achievement.pilotses ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.pilotProfileService
      .query()
      .pipe(map((res: HttpResponse<IPilotProfile[]>) => res.body ?? []))
      .pipe(
        map((pilotProfiles: IPilotProfile[]) =>
          this.pilotProfileService.addPilotProfileToCollectionIfMissing<IPilotProfile>(
            pilotProfiles,
            ...(this.achievement?.pilotses ?? []),
          ),
        ),
      )
      .subscribe((pilotProfiles: IPilotProfile[]) => this.pilotProfilesSharedCollection.set(pilotProfiles));
  }
}
