import { Component, EventEmitter, Input, Output } from '@angular/core';
import { statusBadgeClass } from '../../../../shared/datatable/datatable.utils';
import { NoticeReportDetail } from '../../../../core/models/report.notice';

@Component({
  selector: 'app-notice-detail',
  templateUrl: './notice-detail.component.html'
})
export class NoticeDetailComponent {
  @Input() selectedNotice: NoticeReportDetail | null = null;
  @Input() loadingDetail = false;
  @Input() detailError = '';
  @Input() itemColumns: string[] = [];

  @Output() viewItemLogs = new EventEmitter<number>();

  statusBadge(status: string): string {
    return statusBadgeClass(status);
  }
}
