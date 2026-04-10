import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { NonAuthLayoutComponent } from './layouts/non-auth-layout/non-auth-layout.component';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: NonAuthLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'login' },
      {
        path: 'login',
        loadChildren: () => import('./pages/login/login.module').then((module) => module.LoginModule)
      }
    ]
  },
  {
    path: '',
    component: AuthLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'send-notices',
        loadChildren: () =>
          import('./pages/send-notices/send-notices.module').then((module) => module.SendNoticesModule)
      },
      {
        path: 'notices',
        loadChildren: () =>
          import('./pages/notices-schedule-details/notices-schedule-details.module').then(
            (module) => module.NoticesScheduleDetailsModule
          )
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
