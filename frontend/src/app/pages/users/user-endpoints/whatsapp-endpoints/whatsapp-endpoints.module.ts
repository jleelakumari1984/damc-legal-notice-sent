import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { WhatsappEndpointsComponent } from './whatsapp-endpoints.component';

@NgModule({
  declarations: [WhatsappEndpointsComponent],
  exports: [WhatsappEndpointsComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class WhatsappEndpointsModule {}
