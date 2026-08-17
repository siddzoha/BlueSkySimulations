import { AircraftCategory } from 'app/entities/enumerations/aircraft-category.model';

export interface IAircraft {
  id: number;
  tailNumber?: string | null;
  modelName?: string | null;
  icaoType?: string | null;
  category?: keyof typeof AircraftCategory | null;
  cruiseSpeedKnots?: number | null;
  maxRangeNm?: number | null;
  serviceCeilingFt?: number | null;
  fuelBurnGph?: number | null;
  minRunwayLengthFt?: number | null;
}

export type NewAircraft = Omit<IAircraft, 'id'> & { id: null };
