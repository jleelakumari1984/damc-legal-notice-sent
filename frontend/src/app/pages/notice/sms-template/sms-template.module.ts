import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SmsTemplateComponent } from './sms-template.component';
import { SmsTemplateFormComponent } from './sms-template-form/sms-template-form.component';
import { SmsTemplateFormUserComponent } from './sms-template-form-user/sms-template-form-user.component';
import { SmsTemplateViewComponent } from './sms-template-view/sms-template-view.component';

@NgModule({
  declarations: [SmsTemplateComponent, SmsTemplateFormComponent, SmsTemplateFormUserComponent, SmsTemplateViewComponent],
  exports: [SmsTemplateComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class SmsTemplateModule { }
