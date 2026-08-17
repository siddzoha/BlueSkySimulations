import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AchievementResolve from './route/achievement-routing-resolve.service';

const achievementRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/achievement').then(m => m.Achievement),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/achievement-detail').then(m => m.AchievementDetail),
    resolve: {
      achievement: AchievementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/achievement-update').then(m => m.AchievementUpdate),
    resolve: {
      achievement: AchievementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/achievement-update').then(m => m.AchievementUpdate),
    resolve: {
      achievement: AchievementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default achievementRoute;
