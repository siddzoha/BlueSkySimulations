import { IPilotProfile, NewPilotProfile } from './pilot-profile.model';

export const sampleWithRequiredData: IPilotProfile = {
  id: 23130,
  totalXp: 27744,
  rankTier: 'CAPTAIN',
  totalFlightHours: 23617.88,
  totalNightFlightHours: 25683.89,
  totalIfrFlightHours: 12098.87,
  flightsCompleted: 26828,
};

export const sampleWithPartialData: IPilotProfile = {
  id: 28517,
  totalXp: 1886,
  rankTier: 'SENIOR_FIRST_OFFICER',
  totalFlightHours: 15059.83,
  totalNightFlightHours: 660.87,
  totalIfrFlightHours: 20522.86,
  flightsCompleted: 22374,
};

export const sampleWithFullData: IPilotProfile = {
  id: 1609,
  totalXp: 16564,
  rankTier: 'SENIOR_FIRST_OFFICER',
  totalFlightHours: 18220.02,
  totalNightFlightHours: 31761.62,
  totalIfrFlightHours: 26618.4,
  flightsCompleted: 19592,
};

export const sampleWithNewData: NewPilotProfile = {
  totalXp: 13267,
  rankTier: 'SENIOR_FIRST_OFFICER',
  totalFlightHours: 16800.81,
  totalNightFlightHours: 21288.73,
  totalIfrFlightHours: 28858.66,
  flightsCompleted: 28626,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
