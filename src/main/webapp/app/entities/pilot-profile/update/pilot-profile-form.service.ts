import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPilotProfile, NewPilotProfile } from '../pilot-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPilotProfile for edit and NewPilotProfileFormGroupInput for create.
 */
type PilotProfileFormGroupInput = IPilotProfile | PartialWithRequiredKeyOf<NewPilotProfile>;

type PilotProfileFormDefaults = Pick<NewPilotProfile, 'id' | 'achievementses'>;

type PilotProfileFormGroupContent = {
  id: FormControl<IPilotProfile['id'] | NewPilotProfile['id']>;
  totalXp: FormControl<IPilotProfile['totalXp']>;
  rankTier: FormControl<IPilotProfile['rankTier']>;
  totalFlightHours: FormControl<IPilotProfile['totalFlightHours']>;
  totalNightFlightHours: FormControl<IPilotProfile['totalNightFlightHours']>;
  totalIfrFlightHours: FormControl<IPilotProfile['totalIfrFlightHours']>;
  flightsCompleted: FormControl<IPilotProfile['flightsCompleted']>;
  user: FormControl<IPilotProfile['user']>;
  achievementses: FormControl<IPilotProfile['achievementses']>;
};

export type PilotProfileFormGroup = FormGroup<PilotProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PilotProfileFormService {
  createPilotProfileFormGroup(pilotProfile?: PilotProfileFormGroupInput): PilotProfileFormGroup {
    const pilotProfileRawValue = {
      ...this.getFormDefaults(),
      ...(pilotProfile ?? { id: null }),
    };

    return new FormGroup<PilotProfileFormGroupContent>({
      id: new FormControl(
        { value: pilotProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      totalXp: new FormControl(pilotProfileRawValue.totalXp, {
        validators: [Validators.required, Validators.min(0)],
      }),
      rankTier: new FormControl(pilotProfileRawValue.rankTier, {
        validators: [Validators.required],
      }),
      totalFlightHours: new FormControl(pilotProfileRawValue.totalFlightHours, {
        validators: [Validators.required, Validators.min(0)],
      }),
      totalNightFlightHours: new FormControl(pilotProfileRawValue.totalNightFlightHours, {
        validators: [Validators.required, Validators.min(0)],
      }),
      totalIfrFlightHours: new FormControl(pilotProfileRawValue.totalIfrFlightHours, {
        validators: [Validators.required, Validators.min(0)],
      }),
      flightsCompleted: new FormControl(pilotProfileRawValue.flightsCompleted, {
        validators: [Validators.required, Validators.min(0)],
      }),
      user: new FormControl(pilotProfileRawValue.user),
      achievementses: new FormControl(pilotProfileRawValue.achievementses ?? []),
    });
  }

  getPilotProfile(form: PilotProfileFormGroup): IPilotProfile | NewPilotProfile {
    return form.getRawValue();
  }

  resetForm(form: PilotProfileFormGroup, pilotProfile: PilotProfileFormGroupInput): void {
    const pilotProfileRawValue = { ...this.getFormDefaults(), ...pilotProfile };
    form.reset({
      ...pilotProfileRawValue,
      id: { value: pilotProfileRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PilotProfileFormDefaults {
    return {
      id: null,
      achievementses: [],
    };
  }
}
