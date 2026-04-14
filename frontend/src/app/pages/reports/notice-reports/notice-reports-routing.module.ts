import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NoticeReportsComponent } from './notice-reports.component';

const routes: Routes = [{ path: '', component: NoticeReportsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NoticeReportsRoutingModule {}
