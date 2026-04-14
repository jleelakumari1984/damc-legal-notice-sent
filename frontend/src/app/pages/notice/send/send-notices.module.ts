import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SendNoticesRoutingModule } from './send-notices-routing.module';
import { SendNoticesComponent } from './send-notices.component';

@NgModule({
  declarations: [SendNoticesComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SendNoticesRoutingModule]
})
export class SendNoticesModule {}