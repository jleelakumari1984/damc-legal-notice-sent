import { Component, EventEmitter, Output } from '@angular/core';
import { NoticeReportFilter } from '../../../../core/models/report.notice';

declare const $: any;

@Component({
  selector: 'app-report-filter',
  templateUrl: './report-filter.component.html'
})
export class ReportFilterComponent {
  @Output() filterChange = new EventEmitter<NoticeReportFilter>();

  noticeName = '';
  status = '';
  fromDate: Date | null = null;
  toDate: Date | null = null;

  readonly statuses = ['PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'CANCELLED'];

  open(): void {
    $('#reportFilterModal').modal('show');
  }

  apply(): void {
    this.filterChange.emit(this.buildFilter());
    $('#reportFilterModal').modal('hide');
  }

  reset(): void {
    this.noticeName = '';
    this.status = '';
    this.fromDate = null;
    this.toDate = null;
    this.filterChange.emit({});
    $('#reportFilterModal').modal('hide');
  }

  buildFilter(): NoticeReportFilter {
    const f: NoticeReportFilter = {};
    if (this.noticeName.trim()) f.noticeName = this.noticeName.trim();
    if (this.status) f.status = this.status;
    if (this.fromDate) f.fromDate = this.fromDate;
    if (this.toDate) f.toDate = this.toDate;
    return f;
  }
}
