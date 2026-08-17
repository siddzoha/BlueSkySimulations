import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AircraftResolve from './route/aircraft-routing-resolve.service';

const aircraftRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/aircraft').then(m => m.Aircraft),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/aircraft-detail').then(m => m.AircraftDetail),
    resolve: {
      aircraft: AircraftResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/aircraft-update').then(m => m.AircraftUpdate),
    resolve: {
      aircraft: AircraftResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/aircraft-update').then(m => m.AircraftUpdate),
    resolve: {
      aircraft: AircraftResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default aircraftRoute;
