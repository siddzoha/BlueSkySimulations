import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AirportResolve from './route/airport-routing-resolve.service';

const airportRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/airport').then(m => m.Airport),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/airport-detail').then(m => m.AirportDetail),
    resolve: {
      airport: AirportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/airport-update').then(m => m.AirportUpdate),
    resolve: {
      airport: AirportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/airport-update').then(m => m.AirportUpdate),
    resolve: {
      airport: AirportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default airportRoute;
