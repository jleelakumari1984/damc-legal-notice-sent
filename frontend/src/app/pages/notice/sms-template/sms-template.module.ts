import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SmsTemplateComponent } from './sms-template.component';
import { SmsTemplateFormUserComponent } from './sms-template-form-user/sms-template-form-user.component';
import { SmsTemplateViewComponent } from './sms-template-view/sms-template-view.component';
import { TemplateApprovalsModule } from '../../template-approvals/template-approvals.module';

@NgModule({
  declarations: [SmsTemplateComponent, SmsTemplateFormUserComponent, SmsTemplateViewComponent],
  exports: [SmsTemplateComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, TemplateApprovalsModule]
})
export class SmsTemplateModule { }
