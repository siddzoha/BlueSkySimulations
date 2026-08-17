import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPilotProfile } from '../pilot-profile.model';
import { PilotProfileService } from '../service/pilot-profile.service';

const pilotProfileResolve = (route: ActivatedRouteSnapshot): Observable<null | IPilotProfile> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PilotProfileService);
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

export default pilotProfileResolve;
