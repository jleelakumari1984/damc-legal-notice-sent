import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { WhatsappTemplateComponent } from './whatsapp-template.component';
import { WhatsappTemplateFormComponent } from './whatsapp-template-form/whatsapp-template-form.component';
import { WhatsappTemplateFormUserComponent } from './whatsapp-template-form-user/whatsapp-template-form-user.component';

@NgModule({
  declarations: [WhatsappTemplateComponent, WhatsappTemplateFormComponent, WhatsappTemplateFormUserComponent],
  exports: [WhatsappTemplateComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class WhatsappTemplateModule { }
