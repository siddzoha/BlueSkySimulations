import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IAchievement } from 'app/entities/achievement/achievement.model';
import { AchievementService } from 'app/entities/achievement/service/achievement.service';
import { RankTier } from 'app/entities/enumerations/rank-tier.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPilotProfile } from '../pilot-profile.model';
import { PilotProfileService } from '../service/pilot-profile.service';

import { PilotProfileFormGroup, PilotProfileFormService } from './pilot-profile-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pilot-profile-update',
  templateUrl: './pilot-profile-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PilotProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  pilotProfile: IPilotProfile | null = null;
  rankTierValues = Object.keys(RankTier);

  usersSharedCollection = signal<IUser[]>([]);
  achievementsSharedCollection = signal<IAchievement[]>([]);

  protected pilotProfileService = inject(PilotProfileService);
  protected pilotProfileFormService = inject(PilotProfileFormService);
  protected userService = inject(UserService);
  protected achievementService = inject(AchievementService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PilotProfileFormGroup = this.pilotProfileFormService.createPilotProfileFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareAchievement = (o1: IAchievement | null, o2: IAchievement | null): boolean => this.achievementService.compareAchievement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pilotProfile }) => {
      this.pilotProfile = pilotProfile;
      if (pilotProfile) {
        this.updateForm(pilotProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const pilotProfile = this.pilotProfileFormService.getPilotProfile(this.editForm);
    if (pilotProfile.id === null) {
      this.subscribeToSaveResponse(this.pilotProfileService.create(pilotProfile));
    } else {
      this.subscribeToSaveResponse(this.pilotProfileService.update(pilotProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPilotProfile | null>): void {
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

  protected updateForm(pilotProfile: IPilotProfile): void {
    this.pilotProfile = pilotProfile;
    this.pilotProfileFormService.resetForm(this.editForm, pilotProfile);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, pilotProfile.user));
    this.achievementsSharedCollection.update(achievements =>
      this.achievementService.addAchievementToCollectionIfMissing<IAchievement>(achievements, ...(pilotProfile.achievementses ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.pilotProfile?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.achievementService
      .query()
      .pipe(map((res: HttpResponse<IAchievement[]>) => res.body ?? []))
      .pipe(
        map((achievements: IAchievement[]) =>
          this.achievementService.addAchievementToCollectionIfMissing<IAchievement>(
            achievements,
            ...(this.pilotProfile?.achievementses ?? []),
          ),
        ),
      )
      .subscribe((achievements: IAchievement[]) => this.achievementsSharedCollection.set(achievements));
  }
}
