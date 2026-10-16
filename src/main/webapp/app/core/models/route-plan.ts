import { IAircraft } from '../../entities/aircraft/aircraft.model';
import { IAirport } from '../../entities/airport/airport.model';

export interface RouteProfilePoint {
  latitude: number;
  longitude: number;
  altitudeFt: number;
  distanceFromOriginNm: number;
  verticalSpeedFpm: number;
  flightPhase: 'CLIMB' | 'CRUISE' | 'DESCENT';
}

export interface AircraftWeightLimits {
  emptyWeightLbs: number;
  maxTakeoffWeightLbs: number;
  recommendedTakeoffWeightLbs: number;
}

export interface RoutePlan {
  departureAirport: IAirport;
  arrivalAirport: IAirport;
  aircraft: IAircraft;
  distanceNm: number;
  initHeading: number;
  estFlightTimeHours: number;
  estFuelGallons: number;
  xpReward: number;
  takeoffWeightLbs?: number;
  costIndex?: number;
  targetCruiseAltitudeFt?: number;
  climbRateFpm?: number;
  descentRateFpm?: number;
  profileWaypoints?: RouteProfilePoint[];
}
