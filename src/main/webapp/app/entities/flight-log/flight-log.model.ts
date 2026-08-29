import dayjs from 'dayjs/esm';

import { IAircraft } from 'app/entities/aircraft/aircraft.model';
import { IAirport } from 'app/entities/airport/airport.model';
import { FlightRules } from 'app/entities/enumerations/flight-rules.model';
import { IPilotProfile } from 'app/entities/pilot-profile/pilot-profile.model';

export interface IFlightLog {
  id: number;
  departureTime?: dayjs.Dayjs | null;
  arrivalTime?: dayjs.Dayjs | null;
  durationHours?: number | null;
  distanceNm?: number | null;
  cruiseAltitudeFt?: number | null;
  fuelUsedGallons?: number | null;
  xpEarned?: number | null;
  flightRules?: keyof typeof FlightRules | null;
  remarks?: string | null;
  aircraft?: Pick<IAircraft, 'id' | 'modelName'> | null;
  departureAirport?: Pick<IAirport, 'id' | 'name' | 'icaoCode'> | null;
  arrivalAirport?: Pick<IAirport, 'id' | 'name' | 'icaoCode'> | null;
  pilot?: Pick<IPilotProfile, 'id'> | null;
}

export type NewFlightLog = Omit<IFlightLog, 'id'> & { id: null };
