import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { WhatsappTemplateComponent } from './whatsapp-template.component';
import { WhatsappTemplateFormUserComponent } from './whatsapp-template-form-user/whatsapp-template-form-user.component';
import { WhatsappTemplateViewComponent } from './whatsapp-template-view/whatsapp-template-view.component';
import { TemplateApprovalsModule } from '../../template-approvals/template-approvals.module';

@NgModule({
  declarations: [WhatsappTemplateComponent, WhatsappTemplateFormUserComponent, WhatsappTemplateViewComponent],
  exports: [WhatsappTemplateComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, TemplateApprovalsModule]
})
export class WhatsappTemplateModule { }
