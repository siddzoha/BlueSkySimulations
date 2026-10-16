import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AircraftWeightLimits, RoutePlan } from '../models/route-plan';

@Injectable({
  providedIn: 'root',
})
export class FlightDispatch {
  private http = inject(HttpClient);
  private resourceUrl = '/api/navigation/calculate-route';

  public calculateRoute(
    depAirportId: number,
    arrAirportId: number,
    aircraftId: number,
    takeoffWeightLbs?: number,
    costIndex?: number,
  ): Observable<RoutePlan> {
    let params = new HttpParams()
      .set('departureAirportId', depAirportId)
      .set('arrivalAirportId', arrAirportId)
      .set('aircraftId', aircraftId);

    if (takeoffWeightLbs != null && takeoffWeightLbs > 0) {
      params = params.set('takeoffWeightLbs', takeoffWeightLbs);
    }
    if (costIndex != null && costIndex >= 0) {
      params = params.set('costIndex', costIndex);
    }

    return this.http.get<RoutePlan>(this.resourceUrl, { params });
  }

  public getAircraftWeightLimits(aircraftId: number): Observable<AircraftWeightLimits> {
    return this.http.get<AircraftWeightLimits>(`/api/navigation/aircraft-weight-limits/${aircraftId}`);
  }

  public fileFlight(plan: RoutePlan): Observable<any> {
    return this.http.post<any>('/api/navigation/file-flight', plan);
  }
}
