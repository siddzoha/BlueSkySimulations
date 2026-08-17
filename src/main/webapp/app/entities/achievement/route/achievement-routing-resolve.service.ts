import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IAchievement } from '../achievement.model';
import { AchievementService } from '../service/achievement.service';

const achievementResolve = (route: ActivatedRouteSnapshot): Observable<null | IAchievement> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(AchievementService);
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

export default achievementResolve;
