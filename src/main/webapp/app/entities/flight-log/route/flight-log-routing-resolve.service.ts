import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IFlightLog } from '../flight-log.model';
import { FlightLogService } from '../service/flight-log.service';

const flightLogResolve = (route: ActivatedRouteSnapshot): Observable<null | IFlightLog> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(FlightLogService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default flightLogResolve;
