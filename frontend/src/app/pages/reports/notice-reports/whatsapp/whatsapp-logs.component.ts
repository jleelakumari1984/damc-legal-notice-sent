import { Component, Input } from '@angular/core';
import { statusBadgeClass } from '../../../../shared/datatable/datatable.utils';
import { WhatsappLog } from '../../../../core/models/whatsapp.model';

@Component({
  selector: 'app-whatsapp-logs',
  templateUrl: './whatsapp-logs.component.html'
})
export class WhatsappLogsComponent {
  @Input() whatsappLogs: WhatsappLog[] = [];

  statusBadge(status: string): string {
    return statusBadgeClass(status);
  }
}
