import dayjs from 'dayjs/esm';

import { IFlightLog, NewFlightLog } from './flight-log.model';

export const sampleWithRequiredData: IFlightLog = {
  id: 25650,
  departureTime: dayjs('2023-12-17T19:06'),
  arrivalTime: dayjs('2023-12-17T16:26'),
  durationHours: 6.69,
  distanceNm: 2397,
  flightRules: 'VFR',
};

export const sampleWithPartialData: IFlightLog = {
  id: 23673,
  departureTime: dayjs('2023-12-17T19:22'),
  arrivalTime: dayjs('2023-12-18T00:41'),
  durationHours: 1.29,
  distanceNm: 207,
  cruiseAltitudeFt: 21782,
  flightRules: 'IFR',
};

export const sampleWithFullData: IFlightLog = {
  id: 26065,
  departureTime: dayjs('2023-12-18T01:50'),
  arrivalTime: dayjs('2023-12-17T14:40'),
  durationHours: 20.6,
  distanceNm: 1565,
  cruiseAltitudeFt: 57603,
  fuelUsedGallons: 23573.53,
  xpEarned: 4566,
  flightRules: 'IFR',
  remarks: 'wring boohoo',
};

export const sampleWithNewData: NewFlightLog = {
  departureTime: dayjs('2023-12-17T21:40'),
  arrivalTime: dayjs('2023-12-18T01:44'),
  durationHours: 7.63,
  distanceNm: 4745,
  flightRules: 'IFR',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
