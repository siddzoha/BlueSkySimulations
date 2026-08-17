import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAirport, NewAirport } from '../airport.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAirport for edit and NewAirportFormGroupInput for create.
 */
type AirportFormGroupInput = IAirport | PartialWithRequiredKeyOf<NewAirport>;

type AirportFormDefaults = Pick<NewAirport, 'id'>;

type AirportFormGroupContent = {
  id: FormControl<IAirport['id'] | NewAirport['id']>;
  icaoCode: FormControl<IAirport['icaoCode']>;
  iataCode: FormControl<IAirport['iataCode']>;
  name: FormControl<IAirport['name']>;
  city: FormControl<IAirport['city']>;
  country: FormControl<IAirport['country']>;
  latitude: FormControl<IAirport['latitude']>;
  longitude: FormControl<IAirport['longitude']>;
  elevationFt: FormControl<IAirport['elevationFt']>;
  longestRunwayFt: FormControl<IAirport['longestRunwayFt']>;
};

export type AirportFormGroup = FormGroup<AirportFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AirportFormService {
  createAirportFormGroup(airport?: AirportFormGroupInput): AirportFormGroup {
    const airportRawValue = {
      ...this.getFormDefaults(),
      ...(airport ?? { id: null }),
    };

    return new FormGroup<AirportFormGroupContent>({
      id: new FormControl(
        { value: airportRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      icaoCode: new FormControl(airportRawValue.icaoCode, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(4)],
      }),
      iataCode: new FormControl(airportRawValue.iataCode, {
        validators: [Validators.maxLength(3)],
      }),
      name: new FormControl(airportRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      city: new FormControl(airportRawValue.city, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      country: new FormControl(airportRawValue.country, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      latitude: new FormControl(airportRawValue.latitude, {
        validators: [Validators.required, Validators.min(-90), Validators.max(90)],
      }),
      longitude: new FormControl(airportRawValue.longitude, {
        validators: [Validators.required, Validators.min(-180), Validators.max(180)],
      }),
      elevationFt: new FormControl(airportRawValue.elevationFt, {
        validators: [Validators.min(-1500), Validators.max(15000)],
      }),
      longestRunwayFt: new FormControl(airportRawValue.longestRunwayFt, {
        validators: [Validators.required, Validators.min(500), Validators.max(20000)],
      }),
    });
  }

  getAirport(form: AirportFormGroup): IAirport | NewAirport {
    return form.getRawValue();
  }

  resetForm(form: AirportFormGroup, airport: AirportFormGroupInput): void {
    const airportRawValue = { ...this.getFormDefaults(), ...airport };
    form.reset({
      ...airportRawValue,
      id: { value: airportRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AirportFormDefaults {
    return {
      id: null,
    };
  }
}
