import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NoticeReportsRoutingModule } from './notice-reports-routing.module';
import { NoticeReportsComponent } from './notice-reports.component';
import { NoticeDetailComponent } from './detils/notice-detail.component';
import { SmsLogsComponent } from './sms/sms-logs.component';
import { WhatsappLogsComponent } from './whatsapp/whatsapp-logs.component';
import { ReportFilterComponent } from './report-filter/report-filter.component';

@NgModule({
  declarations: [NoticeReportsComponent, NoticeDetailComponent, SmsLogsComponent, WhatsappLogsComponent, ReportFilterComponent],
  imports: [CommonModule, FormsModule, NoticeReportsRoutingModule]
})
export class NoticeReportsModule {}
