import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NoticeReportsRoutingModule } from './notice-reports-routing.module';
import { NoticeReportsComponent } from './notice-reports.component';
import { NoticeDetailComponent } from './detils/notice-detail.component';
import { SmsLogsComponent } from './sms/sms-logs.component';
import { WhatsappLogsComponent } from './whatsapp/whatsapp-logs.component';

@NgModule({
  declarations: [NoticeReportsComponent, NoticeDetailComponent, SmsLogsComponent, WhatsappLogsComponent],
  imports: [CommonModule, NoticeReportsRoutingModule]
})
export class NoticeReportsModule {}
