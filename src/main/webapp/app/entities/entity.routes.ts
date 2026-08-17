import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'aircraft',
    data: { pageTitle: 'Aircrafts' },
    loadChildren: () => import('./aircraft/aircraft.routes'),
  },
  {
    path: 'airport',
    data: { pageTitle: 'Airports' },
    loadChildren: () => import('./airport/airport.routes'),
  },
  {
    path: 'flight-log',
    data: { pageTitle: 'FlightLogs' },
    loadChildren: () => import('./flight-log/flight-log.routes'),
  },
  {
    path: 'pilot-profile',
    data: { pageTitle: 'PilotProfiles' },
    loadChildren: () => import('./pilot-profile/pilot-profile.routes'),
  },
  {
    path: 'achievement',
    data: { pageTitle: 'Achievements' },
    loadChildren: () => import('./achievement/achievement.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'UserManagements' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
