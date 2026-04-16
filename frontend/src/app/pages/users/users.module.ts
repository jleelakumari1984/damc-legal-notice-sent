import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { UsersRoutingModule } from './users-routing.module';
import { UsersComponent } from './users.component';
import { UserFormComponent } from './user-form/user-form.component';
import { UserFilterComponent } from './user-filter/user-filter.component';
import { UserEndpointsComponent } from './user-endpoints/user-endpoints.component';
import { CreditModule } from './credit/credit.module';
import { SmsEndpointsModule } from './user-endpoints/sms-endpoints/sms-endpoints.module';
import { WhatsappEndpointsModule } from './user-endpoints/whatsapp-endpoints/whatsapp-endpoints.module';

@NgModule({
  declarations: [UsersComponent, UserFormComponent, UserFilterComponent, UserEndpointsComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, UsersRoutingModule, CreditModule, SmsEndpointsModule, WhatsappEndpointsModule]
})
export class UsersModule {}
