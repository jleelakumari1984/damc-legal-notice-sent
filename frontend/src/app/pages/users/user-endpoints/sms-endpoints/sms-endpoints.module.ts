import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SmsEndpointsComponent } from './sms-endpoints.component';

@NgModule({
  declarations: [SmsEndpointsComponent],
  exports: [SmsEndpointsComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class SmsEndpointsModule {}
