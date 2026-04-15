import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TemplateApprovalsRoutingModule } from './template-approvals-routing.module';
import { TemplateApprovalsComponent } from './template-approvals.component';
import { SmsApprovalFormComponent } from './sms-approval-form/sms-approval-form.component';
import { WhatsappApprovalFormComponent } from './whatsapp-approval-form/whatsapp-approval-form.component';

@NgModule({
  declarations: [
    TemplateApprovalsComponent,
    SmsApprovalFormComponent,
    WhatsappApprovalFormComponent
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, TemplateApprovalsRoutingModule]
})
export class TemplateApprovalsModule {}
