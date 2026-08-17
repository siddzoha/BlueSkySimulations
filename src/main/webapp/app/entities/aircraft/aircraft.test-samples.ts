import { IAircraft, NewAircraft } from './aircraft.model';

export const sampleWithRequiredData: IAircraft = {
  id: 29140,
  tailNumber: 'pants supp',
  modelName: 'lightly',
  icaoType: 'like',
  category: 'PISTON_SINGLE',
  cruiseSpeedKnots: 233,
  maxRangeNm: 9968,
  fuelBurnGph: 4869.76,
  minRunwayLengthFt: 5885,
};

export const sampleWithPartialData: IAircraft = {
  id: 11823,
  tailNumber: 'cruelly',
  modelName: 'or likewise numeric',
  icaoType: 'lead',
  category: 'REGIONAL_JET',
  cruiseSpeedKnots: 72,
  maxRangeNm: 2840,
  serviceCeilingFt: 46534,
  fuelBurnGph: 3253.02,
  minRunwayLengthFt: 14338,
};

export const sampleWithFullData: IAircraft = {
  id: 4507,
  tailNumber: 'unimpressi',
  modelName: 'exhausted',
  icaoType: 'spla',
  category: 'WIDEBODY_JET',
  cruiseSpeedKnots: 148,
  maxRangeNm: 4477,
  serviceCeilingFt: 27159,
  fuelBurnGph: 110.32,
  minRunwayLengthFt: 13506,
};

export const sampleWithNewData: NewAircraft = {
  tailNumber: 'what blend',
  modelName: 'flimsy',
  icaoType: 'whol',
  category: 'NARROWBODY_JET',
  cruiseSpeedKnots: 395,
  maxRangeNm: 5750,
  fuelBurnGph: 1473.97,
  minRunwayLengthFt: 12059,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
