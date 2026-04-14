import { AfterViewInit, Component, OnInit } from '@angular/core';

import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { NoticeReportsDatatable } from '../../../shared/datatable/notice-reports-datatable';
 
import { statusBadgeClass } from '../../../shared/datatable/datatable.utils';
import { NoticeReportDetail, NoticeReportItemDetail } from '../../../core/models/report.notice';
import { NoticeReportsService } from '../../../core/services/notice-reports.service';

declare const $: any;

@Component({
  selector: 'app-notice-reports',
  templateUrl: './notice-reports.component.html',
  styleUrls: ['./notice-reports.component.css']
})
export class NoticeReportsComponent implements OnInit, AfterViewInit {
  selectedNotice: NoticeReportDetail | null = null;
  loadingDetail = false;
  errorMessage = '';
  detailError = '';
  itemColumns: string[] = [];

  // Item logs modal
  selectedItemDetail: NoticeReportItemDetail | null = null;
  loadingItemDetail = false;
  itemDetailError = '';
  activeLogTab: 'sms' | 'whatsapp' | 'error' = 'sms';

  constructor(
    private readonly service: NoticeReportsService,
    private readonly datatableHelper: DatatableHelper
  ) { }

  ngOnInit(): void { }

  ngAfterViewInit(): void {
    this.initTable();
  }

  private initTable(): void {
    this.datatableHelper.initTable('#noticesTable', new NoticeReportsDatatable({
      onViewDetail: (id) => this.viewDetail(id),
      onError: (msg) => this.errorMessage = msg
    }));
  }

  reload(): void {
    this.errorMessage = '';
    this.datatableHelper.reload('#noticesTable');
  }

  viewDetail(id: number): void {
    if (this.selectedNotice && this.selectedNotice.summary.id === id) {
      return; // Prevent multiple clicks
    }
    this.loadingDetail = true;
    this.detailError = '';
    this.selectedNotice = null;
    this.service.getById(id).subscribe({
      next: (detail) => {
        this.selectedNotice = detail;
        this.loadingDetail = false;
        this.itemColumns = detail.items?.length
          ? Object.keys(detail.items[0].excelData ?? {})
          : [];
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

  openItemLogs(itemId: number): void {
    this.selectedItemDetail = null;
    this.itemDetailError = '';
    this.loadingItemDetail = true;
    this.activeLogTab = 'sms';
    $('#itemLogsModal').modal('show');

    this.service.getItemDetail(itemId).subscribe({
      next: (detail) => {
        this.selectedItemDetail = detail;
        this.loadingItemDetail = false;
      },
      error: () => {
        this.loadingItemDetail = false;
        this.itemDetailError = 'Failed to load item details.';
      }
    });
  }

  closeItemLogs(): void {
    $('#itemLogsModal').modal('hide');
    this.selectedItemDetail = null;
    this.itemDetailError = '';
  }

  statusBadge(status: string): string {
    return statusBadgeClass(status);
  }

  objectKeys(obj: Record<string, unknown>): string[] {
    return obj ? Object.keys(obj) : [];
  }
}
