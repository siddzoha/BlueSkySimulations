import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFlightLog, NewFlightLog } from '../flight-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFlightLog for edit and NewFlightLogFormGroupInput for create.
 */
type FlightLogFormGroupInput = IFlightLog | PartialWithRequiredKeyOf<NewFlightLog>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFlightLog | NewFlightLog> = Omit<T, 'departureTime' | 'arrivalTime'> & {
  departureTime?: string | null;
  arrivalTime?: string | null;
};

type FlightLogFormRawValue = FormValueOf<IFlightLog>;

type NewFlightLogFormRawValue = FormValueOf<NewFlightLog>;

type FlightLogFormDefaults = Pick<NewFlightLog, 'id' | 'departureTime' | 'arrivalTime'>;

type FlightLogFormGroupContent = {
  id: FormControl<FlightLogFormRawValue['id'] | NewFlightLog['id']>;
  departureTime: FormControl<FlightLogFormRawValue['departureTime']>;
  arrivalTime: FormControl<FlightLogFormRawValue['arrivalTime']>;
  durationHours: FormControl<FlightLogFormRawValue['durationHours']>;
  distanceNm: FormControl<FlightLogFormRawValue['distanceNm']>;
  cruiseAltitudeFt: FormControl<FlightLogFormRawValue['cruiseAltitudeFt']>;
  fuelUsedGallons: FormControl<FlightLogFormRawValue['fuelUsedGallons']>;
  xpEarned: FormControl<FlightLogFormRawValue['xpEarned']>;
  flightRules: FormControl<FlightLogFormRawValue['flightRules']>;
  remarks: FormControl<FlightLogFormRawValue['remarks']>;
  aircraft: FormControl<FlightLogFormRawValue['aircraft']>;
  departureAirport: FormControl<FlightLogFormRawValue['departureAirport']>;
  arrivalAirport: FormControl<FlightLogFormRawValue['arrivalAirport']>;
  pilot: FormControl<FlightLogFormRawValue['pilot']>;
};

export type FlightLogFormGroup = FormGroup<FlightLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FlightLogFormService {
  createFlightLogFormGroup(flightLog?: FlightLogFormGroupInput): FlightLogFormGroup {
    const flightLogRawValue = this.convertFlightLogToFlightLogRawValue({
      ...this.getFormDefaults(),
      ...(flightLog ?? { id: null }),
    });

    return new FormGroup<FlightLogFormGroupContent>({
      id: new FormControl(
        { value: flightLogRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      departureTime: new FormControl(flightLogRawValue.departureTime, {
        validators: [Validators.required],
      }),
      arrivalTime: new FormControl(flightLogRawValue.arrivalTime, {
        validators: [Validators.required],
      }),
      durationHours: new FormControl(flightLogRawValue.durationHours, {
        validators: [Validators.required, Validators.min(0), Validators.max(30)],
      }),
      distanceNm: new FormControl(flightLogRawValue.distanceNm, {
        validators: [Validators.required, Validators.min(1), Validators.max(15000)],
      }),
      cruiseAltitudeFt: new FormControl(flightLogRawValue.cruiseAltitudeFt, {
        validators: [Validators.min(1000), Validators.max(60000)],
      }),
      fuelUsedGallons: new FormControl(flightLogRawValue.fuelUsedGallons, {
        validators: [Validators.min(0)],
      }),
      xpEarned: new FormControl(flightLogRawValue.xpEarned, {
        validators: [Validators.min(0)],
      }),
      flightRules: new FormControl(flightLogRawValue.flightRules, {
        validators: [Validators.required],
      }),
      remarks: new FormControl(flightLogRawValue.remarks, {
        validators: [Validators.maxLength(500)],
      }),
      aircraft: new FormControl(flightLogRawValue.aircraft),
      departureAirport: new FormControl(flightLogRawValue.departureAirport),
      arrivalAirport: new FormControl(flightLogRawValue.arrivalAirport),
      pilot: new FormControl(flightLogRawValue.pilot),
    });
  }

  getFlightLog(form: FlightLogFormGroup): IFlightLog | NewFlightLog {
    return this.convertFlightLogRawValueToFlightLog(form.getRawValue());
  }

  resetForm(form: FlightLogFormGroup, flightLog: FlightLogFormGroupInput): void {
    const flightLogRawValue = this.convertFlightLogToFlightLogRawValue({ ...this.getFormDefaults(), ...flightLog });
    form.reset({
      ...flightLogRawValue,
      id: { value: flightLogRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): FlightLogFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      departureTime: currentTime,
      arrivalTime: currentTime,
    };
  }

  private convertFlightLogRawValueToFlightLog(rawFlightLog: FlightLogFormRawValue | NewFlightLogFormRawValue): IFlightLog | NewFlightLog {
    return {
      ...rawFlightLog,
      departureTime: dayjs(rawFlightLog.departureTime, DATE_TIME_FORMAT),
      arrivalTime: dayjs(rawFlightLog.arrivalTime, DATE_TIME_FORMAT),
    };
  }

  private convertFlightLogToFlightLogRawValue(
    flightLog: IFlightLog | (Partial<NewFlightLog> & FlightLogFormDefaults),
  ): FlightLogFormRawValue | PartialWithRequiredKeyOf<NewFlightLogFormRawValue> {
    return {
      ...flightLog,
      departureTime: flightLog.departureTime ? flightLog.departureTime.format(DATE_TIME_FORMAT) : undefined,
      arrivalTime: flightLog.arrivalTime ? flightLog.arrivalTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
