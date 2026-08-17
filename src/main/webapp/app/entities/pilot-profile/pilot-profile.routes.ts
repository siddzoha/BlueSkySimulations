import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PilotProfileResolve from './route/pilot-profile-routing-resolve.service';

const pilotProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/pilot-profile').then(m => m.PilotProfile),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/pilot-profile-detail').then(m => m.PilotProfileDetail),
    resolve: {
      pilotProfile: PilotProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/pilot-profile-update').then(m => m.PilotProfileUpdate),
    resolve: {
      pilotProfile: PilotProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/pilot-profile-update').then(m => m.PilotProfileUpdate),
    resolve: {
      pilotProfile: PilotProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default pilotProfileRoute;
