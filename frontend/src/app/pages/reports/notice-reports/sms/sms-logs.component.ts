import { Component, Input } from '@angular/core';
import { statusBadgeClass } from '../../../../shared/datatable/datatable.utils';
import { SmsLog } from '../../../../core/models/sms.model';

@Component({
  selector: 'app-sms-logs',
  templateUrl: './sms-logs.component.html'
})
export class SmsLogsComponent {
  @Input() smsLogs: SmsLog[] = [];

  statusBadge(status: string): string {
    return statusBadgeClass(status);
  }
}
