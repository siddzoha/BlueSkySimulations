import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import FlightLogResolve from './route/flight-log-routing-resolve.service';

const flightLogRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/flight-log').then(m => m.FlightLog),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/flight-log-detail').then(m => m.FlightLogDetail),
    resolve: {
      flightLog: FlightLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/flight-log-update').then(m => m.FlightLogUpdate),
    resolve: {
      flightLog: FlightLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/flight-log-update').then(m => m.FlightLogUpdate),
    resolve: {
      flightLog: FlightLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default flightLogRoute;
