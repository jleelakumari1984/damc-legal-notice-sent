import { AfterViewInit, Component, OnInit } from '@angular/core';

import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { NoticeReportsDatatable } from '../../../shared/datatable/notice-reports-datatable';

import { statusBadgeClass } from '../../../shared/datatable/datatable.utils';
import { NoticeReportDetail, NoticeReportItemDetail, NoticeReportSummary } from '../../../core/models/report.notice';
import { NoticeReportsService } from '../../../core/services/notice-reports.service';

declare const $: any;

@Component({
  selector: 'app-notice-reports',
  templateUrl: './notice-reports.component.html',
  styleUrls: ['./notice-reports.component.css']
})
export class NoticeReportsComponent implements OnInit, AfterViewInit {

  selectedReport: NoticeReportSummary | null = null;
  errorMessage = '';
  actionType: string = 'display';
  reportStatus = '';
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
      service: this.service,
      callbacks: {
        onViewDetail: (id, status, itemCount) => this.viewDetail(id, status, itemCount),
        onSmsDetail: (id) => this.viewSmsLogs(id),
        onWhatsappDetail: (id) => this.viewWhatsappLogs(id),
        onError: (msg) => this.errorMessage = msg
      }
    }));
  }
  viewSmsLogs(selectedNotice: NoticeReportSummary): void {
    if (this.actionType === 'sms' && ((this.selectedReport && this.selectedReport.id === selectedNotice.id))) {
      return; // Prevent multiple clicks
    }
    this.actionType = 'sms';
    this.reportStatus = '';
    this.selectedReport = selectedNotice;
  }
  viewWhatsappLogs(selectedNotice: NoticeReportSummary): void {
    if (this.actionType === 'whatsapp' && ((this.selectedReport && this.selectedReport.id === selectedNotice.id))) {
      return; // Prevent multiple clicks
    }
    this.actionType = 'whatsapp';
    this.reportStatus = '';
    this.selectedReport = selectedNotice;
  }

  reload(): void {
    this.errorMessage = '';
    this.datatableHelper.reload('#noticesTable');

  }

  viewDetail(selectedReport: NoticeReportSummary, status: string, itemCount: number): void {
    if (this.actionType === 'detail' && ((this.selectedReport && this.selectedReport.id === selectedReport.id) || (itemCount && itemCount <= 0))) {
      return; // Prevent multiple clicks
    }
    this.actionType = 'detail';
    this.reportStatus = status;
    this.selectedReport = selectedReport;

  }


  statusBadge(status: string): string {
    return statusBadgeClass(status);
  }

  objectKeys(obj: Record<string, unknown>): string[] {
    return obj ? Object.keys(obj) : [];
  }
  showDisplayDetail() {
    this.actionType = 'display';
    this.selectedReport = null;
  }
}
