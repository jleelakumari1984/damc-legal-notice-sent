import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NoticesScheduleDetailsComponent } from './notices-schedule-details.component';

const routes: Routes = [{ path: '', component: NoticesScheduleDetailsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NoticesScheduleDetailsRoutingModule {}
