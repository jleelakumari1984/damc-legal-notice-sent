import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SmsEndpointsRoutingModule } from './sms-endpoints-routing.module';
import { SmsEndpointsComponent } from './sms-endpoints.component';

@NgModule({
  declarations: [SmsEndpointsComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SmsEndpointsRoutingModule]
})
export class SmsEndpointsModule {}
