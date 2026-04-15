import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { WhatsappEndpointsRoutingModule } from './whatsapp-endpoints-routing.module';
import { WhatsappEndpointsComponent } from './whatsapp-endpoints.component';

@NgModule({
  declarations: [WhatsappEndpointsComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, WhatsappEndpointsRoutingModule]
})
export class WhatsappEndpointsModule {}
