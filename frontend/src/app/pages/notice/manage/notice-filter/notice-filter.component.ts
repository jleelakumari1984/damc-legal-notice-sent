import { Component, EventEmitter, Output } from '@angular/core';
import { NoticeReportFilter } from '../../../../core/models/report.notice';

declare const $: any;

@Component({
  selector: 'app-notice-filter',
  templateUrl: './notice-filter.component.html'
})
export class NoticeFilterComponent {
  @Output() filterChange = new EventEmitter<NoticeReportFilter>();

  name = '';

  open(): void {
    $('#noticeFilterModal').modal('show');
  }

  apply(): void {
    this.filterChange.emit(this.buildFilter());
    $('#noticeFilterModal').modal('hide');
  }

  reset(): void {
    this.name = '';
    this.filterChange.emit({});
    $('#noticeFilterModal').modal('hide');
  }

  buildFilter(): NoticeReportFilter {
    const f: NoticeReportFilter = {};
    if (this.name.trim()) f.noticeName = this.name.trim();
    return f;
  }
}
