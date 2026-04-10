import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NoticesScheduleDetailsRoutingModule } from './notices-schedule-details-routing.module';
import { NoticesScheduleDetailsComponent } from './notices-schedule-details.component';

@NgModule({
  declarations: [NoticesScheduleDetailsComponent],
  imports: [CommonModule, NoticesScheduleDetailsRoutingModule]
})
export class NoticesScheduleDetailsModule {}
