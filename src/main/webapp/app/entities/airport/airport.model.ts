export interface IAirport {
  id: number;
  icaoCode?: string | null;
  iataCode?: string | null;
  name?: string | null;
  city?: string | null;
  country?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  elevationFt?: number | null;
  longestRunwayFt?: number | null;
}

export type NewAirport = Omit<IAirport, 'id'> & { id: null };
