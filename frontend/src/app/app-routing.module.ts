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
          import('./pages/notice/send/send-notices.module').then((module) => module.SendNoticesModule)
      },
      {
        path: 'reports',
        loadChildren: () =>
          import('./pages/reports/notice-reports/notice-reports.module').then(
            (module) => module.NoticeReportsModule
          )
      },
      {
        path: 'notice',
        loadChildren: () =>
          import('./pages/notice/manage/notice.module').then((module) => module.NoticeModule)
      },
      {
        path: 'users',
        loadChildren: () =>
          import('./pages/users/users.module').then((module) => module.UsersModule)
      },
      {
        path: 'credit',
        loadChildren: () =>
          import('./pages/credit/credit.module').then((module) => module.CreditModule)
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
