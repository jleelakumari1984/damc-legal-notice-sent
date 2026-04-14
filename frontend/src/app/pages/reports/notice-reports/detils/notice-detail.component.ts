import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { statusBadgeClass } from '../../../../shared/datatable/datatable.utils';
import { NoticeReportDetail, NoticeReportItem, NoticeReportSummary } from '../../../../core/models/report.notice';
import { NoticeReportsService } from '../../../../core/services/notice-reports.service';
import { DatatableHelper } from '../../../../shared/datatable/datatable.helper';

@Component({
  selector: 'app-notice-detail',
  templateUrl: './notice-detail.component.html'
})
export class NoticeDetailComponent implements OnInit {
  @Input() selectedReport: NoticeReportSummary | null = null;
  @Input() reportStatus: string = '';
  loadingDetail = false;
  detailError = '';
  itemColumns: string[] = [];
  itemDetails: NoticeReportItem[] | null = null;
  @Output() viewItemLogs = new EventEmitter<number>();
  @Output() onClose = new EventEmitter<void>();

  constructor(
    private readonly service: NoticeReportsService,
    private readonly datatableHelper: DatatableHelper
  ) { }
  statusBadge(status: string): string {
    return statusBadgeClass(status);
  }

  ngOnInit(): void {

    this.loadDetails();


  }
  loadDetails() {
    if (this.selectedReport) {
      this.service.getDetails(this.selectedReport?.id, this.reportStatus).subscribe({
        next: (detail) => {
          this.loadingDetail = false;
          this.itemColumns = detail.items?.length
            ? Object.keys(detail.items[0].excelData ?? {})
            : [];
          this.itemDetails = detail.items ?? [];
          setTimeout(() => {
            this.datatableHelper.init('#noticeItemsTable');
            document.getElementById('detailSection')?.scrollIntoView({ behavior: 'smooth' });
          }, 100);
        },
        error: () => {
          this.loadingDetail = false;
          this.detailError = 'Failed to load notice details.';
        }
      });
    }
  }
}
