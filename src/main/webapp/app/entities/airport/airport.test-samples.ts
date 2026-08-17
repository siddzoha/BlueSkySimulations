import { IAirport, NewAirport } from './airport.model';

export const sampleWithRequiredData: IAirport = {
  id: 7214,
  icaoCode: 'gadz',
  name: 'about absent intently',
  city: 'East Liliane',
  country: 'Saint Helena',
  latitude: 48.57,
  longitude: -54.28,
  longestRunwayFt: 8401,
};

export const sampleWithPartialData: IAirport = {
  id: 29396,
  icaoCode: 'peni',
  iataCode: 'ver',
  name: 'bah jealously',
  city: 'Emilioworth',
  country: 'Maldives',
  latitude: -29.12,
  longitude: 158.69,
  elevationFt: 462,
  longestRunwayFt: 13788,
};

export const sampleWithFullData: IAirport = {
  id: 3794,
  icaoCode: 'unti',
  iataCode: 'tec',
  name: 'knottily neatly',
  city: 'East Brenna',
  country: 'Guadeloupe',
  latitude: 43.43,
  longitude: -113.97,
  elevationFt: 2980,
  longestRunwayFt: 10940,
};

export const sampleWithNewData: NewAirport = {
  icaoCode: 'that',
  name: 'nudge yowza within',
  city: 'East Orinland',
  country: 'Uzbekistan',
  latitude: 83.32,
  longitude: 23.09,
  longestRunwayFt: 14729,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
