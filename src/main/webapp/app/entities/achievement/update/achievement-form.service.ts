import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAchievement, NewAchievement } from '../achievement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAchievement for edit and NewAchievementFormGroupInput for create.
 */
type AchievementFormGroupInput = IAchievement | PartialWithRequiredKeyOf<NewAchievement>;

type AchievementFormDefaults = Pick<NewAchievement, 'id' | 'pilotses'>;

type AchievementFormGroupContent = {
  id: FormControl<IAchievement['id'] | NewAchievement['id']>;
  code: FormControl<IAchievement['code']>;
  title: FormControl<IAchievement['title']>;
  description: FormControl<IAchievement['description']>;
  badgeIcon: FormControl<IAchievement['badgeIcon']>;
  xpReward: FormControl<IAchievement['xpReward']>;
  pilotses: FormControl<IAchievement['pilotses']>;
};

export type AchievementFormGroup = FormGroup<AchievementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AchievementFormService {
  createAchievementFormGroup(achievement?: AchievementFormGroupInput): AchievementFormGroup {
    const achievementRawValue = {
      ...this.getFormDefaults(),
      ...(achievement ?? { id: null }),
    };

    return new FormGroup<AchievementFormGroupContent>({
      id: new FormControl(
        { value: achievementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(achievementRawValue.code, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      title: new FormControl(achievementRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(achievementRawValue.description, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      badgeIcon: new FormControl(achievementRawValue.badgeIcon, {
        validators: [Validators.maxLength(50)],
      }),
      xpReward: new FormControl(achievementRawValue.xpReward, {
        validators: [Validators.required, Validators.min(0)],
      }),
      pilotses: new FormControl(achievementRawValue.pilotses ?? []),
    });
  }

  getAchievement(form: AchievementFormGroup): IAchievement | NewAchievement {
    return form.getRawValue();
  }

  resetForm(form: AchievementFormGroup, achievement: AchievementFormGroupInput): void {
    const achievementRawValue = { ...this.getFormDefaults(), ...achievement };
    form.reset({
      ...achievementRawValue,
      id: { value: achievementRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AchievementFormDefaults {
    return {
      id: null,
      pilotses: [],
    };
  }
}
