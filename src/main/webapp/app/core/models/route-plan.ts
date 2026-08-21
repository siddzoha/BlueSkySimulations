import { IAircraft } from '../../entities/aircraft/aircraft.model';
import { IAirport } from '../../entities/airport/airport.model';

export interface RoutePlan {
  departureAirport: IAirport;
  arrivalAirport: IAirport;
  aircraft: IAircraft;
  distanceNm: number;
  initHeading: number;
  estFlightTimeHours: number;
  estFuelGallons: number;
  xpReward: number;
}
