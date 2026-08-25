import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoutePlan } from '../models/route-plan';

@Injectable({
  providedIn: 'root',
})
export class FlightDispatch {
  private http = inject(HttpClient);
  private resourceUrl = '/api/navigation/calculate-route';

  public calculateRoute(depAirportId: number, arrAirportId: number, aircraftId: number): Observable<RoutePlan> {
    const params = new HttpParams()
      .set('departureAirportId', depAirportId)
      .set('arrivalAirportId', arrAirportId)
      .set('aircraftId', aircraftId);

    return this.http.get<RoutePlan>(this.resourceUrl, { params });
  }

  public fileFlight(plan: RoutePlan): Observable<any> {
    return this.http.post<any>('/api/navigation/file-flight', plan);
  }
}
