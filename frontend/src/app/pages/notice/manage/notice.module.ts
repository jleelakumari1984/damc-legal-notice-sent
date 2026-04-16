import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NoticeRoutingModule } from './notice-routing.module';
import { NoticeComponent } from './notice.component';
import { NoticeFormComponent } from './notice-form/notice-form.component';
import { NoticeFilterComponent } from './notice-filter/notice-filter.component';
import { NoticeExcelMappingsModule } from '../excel-mappings/notice-excel-mappings.module';
import { SmsTemplateModule } from '../sms-template/sms-template.module';
import { WhatsappTemplateModule } from '../whatsapp-template/whatsapp-template.module';

@NgModule({
  declarations: [NoticeComponent, NoticeFormComponent, NoticeFilterComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NoticeRoutingModule,
            NoticeExcelMappingsModule, SmsTemplateModule, WhatsappTemplateModule]
})
export class NoticeModule { }
