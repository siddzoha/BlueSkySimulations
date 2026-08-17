import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAircraft, NewAircraft } from '../aircraft.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAircraft for edit and NewAircraftFormGroupInput for create.
 */
type AircraftFormGroupInput = IAircraft | PartialWithRequiredKeyOf<NewAircraft>;

type AircraftFormDefaults = Pick<NewAircraft, 'id'>;

type AircraftFormGroupContent = {
  id: FormControl<IAircraft['id'] | NewAircraft['id']>;
  tailNumber: FormControl<IAircraft['tailNumber']>;
  modelName: FormControl<IAircraft['modelName']>;
  icaoType: FormControl<IAircraft['icaoType']>;
  category: FormControl<IAircraft['category']>;
  cruiseSpeedKnots: FormControl<IAircraft['cruiseSpeedKnots']>;
  maxRangeNm: FormControl<IAircraft['maxRangeNm']>;
  serviceCeilingFt: FormControl<IAircraft['serviceCeilingFt']>;
  fuelBurnGph: FormControl<IAircraft['fuelBurnGph']>;
  minRunwayLengthFt: FormControl<IAircraft['minRunwayLengthFt']>;
};

export type AircraftFormGroup = FormGroup<AircraftFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AircraftFormService {
  createAircraftFormGroup(aircraft?: AircraftFormGroupInput): AircraftFormGroup {
    const aircraftRawValue = {
      ...this.getFormDefaults(),
      ...(aircraft ?? { id: null }),
    };

    return new FormGroup<AircraftFormGroupContent>({
      id: new FormControl(
        { value: aircraftRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tailNumber: new FormControl(aircraftRawValue.tailNumber, {
        validators: [Validators.required, Validators.maxLength(10)],
      }),
      modelName: new FormControl(aircraftRawValue.modelName, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      icaoType: new FormControl(aircraftRawValue.icaoType, {
        validators: [Validators.required, Validators.maxLength(4)],
      }),
      category: new FormControl(aircraftRawValue.category, {
        validators: [Validators.required],
      }),
      cruiseSpeedKnots: new FormControl(aircraftRawValue.cruiseSpeedKnots, {
        validators: [Validators.required, Validators.min(40), Validators.max(600)],
      }),
      maxRangeNm: new FormControl(aircraftRawValue.maxRangeNm, {
        validators: [Validators.required, Validators.min(100), Validators.max(10000)],
      }),
      serviceCeilingFt: new FormControl(aircraftRawValue.serviceCeilingFt, {
        validators: [Validators.min(1000), Validators.max(60000)],
      }),
      fuelBurnGph: new FormControl(aircraftRawValue.fuelBurnGph, {
        validators: [Validators.required, Validators.min(1), Validators.max(5000)],
      }),
      minRunwayLengthFt: new FormControl(aircraftRawValue.minRunwayLengthFt, {
        validators: [Validators.required, Validators.min(500), Validators.max(15000)],
      }),
    });
  }

  getAircraft(form: AircraftFormGroup): IAircraft | NewAircraft {
    return form.getRawValue();
  }

  resetForm(form: AircraftFormGroup, aircraft: AircraftFormGroupInput): void {
    const aircraftRawValue = { ...this.getFormDefaults(), ...aircraft };
    form.reset({
      ...aircraftRawValue,
      id: { value: aircraftRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AircraftFormDefaults {
    return {
      id: null,
    };
  }
}
