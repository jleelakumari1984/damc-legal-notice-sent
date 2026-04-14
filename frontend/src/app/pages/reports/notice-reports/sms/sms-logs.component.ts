import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NoticeReportSummary } from '../../../../core/models/report.notice';
import { DatatableHelper } from '../../../../shared/datatable/datatable.helper';
import { SmsLogsDatatable } from '../../../../shared/datatable/sms-logs-datatable';
import { NoticeReportsService } from '../../../../core/services/notice-reports.service';

@Component({
  selector: 'app-sms-logs',
  templateUrl: './sms-logs.component.html'
})
export class SmsLogsComponent implements OnInit, AfterViewInit {
  @Input() selectedReport: NoticeReportSummary | null = null;
  @Input() reportStatus: string = '';
  @Output() onClose = new EventEmitter<void>();

  errorMessage = '';
  activeTab: 'SUCCESS' | 'ERROR' = 'SUCCESS';
  downloading = false;
  private lastRequest: any = null;

  constructor(
    private readonly datatableHelper: DatatableHelper,
    private readonly service: NoticeReportsService
  ) { }

  ngOnInit(): void { }

  ngAfterViewInit(): void {
    if (this.selectedReport) {
      this.initTable();
      setTimeout(() => document.getElementById('smsDetailSection')?.scrollIntoView({ behavior: 'smooth' }), 100);
    }
  }

  switchTab(tab: 'SUCCESS' | 'ERROR'): void {
    if (this.activeTab === tab) return;
    this.activeTab = tab;
    this.reportStatus = tab.toLowerCase();
    this.errorMessage = '';
    this.datatableHelper.destroy('#smsLogsTable');
    this.initTable();
  }

  downloadExcel(): void {
    if (!this.selectedReport || this.downloading) return;
    this.downloading = true;
    this.errorMessage = '';
    const request = {
      sortColumn: 'sendAt', sortDirection: 'desc',
      dtStart: 0, dtLength: -1, dtDraw: 1,
      status: this.reportStatus
    };
    const source$ = this.activeTab === 'ERROR'
      ? this.service.exportErrorSmsLogs(this.selectedReport.id, request)
      : this.service.exportSmsLogs(this.selectedReport.id, request);
    source$.subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `sms-logs-${this.selectedReport!.id}-${this.activeTab.toLowerCase()}.csv`;
        a.click();
        URL.revokeObjectURL(url);
        this.downloading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to download CSV.';
        this.downloading = false;
      }
    });
  }

  private initTable(): void {
    this.datatableHelper.initTable('#smsLogsTable', new SmsLogsDatatable({
      reportId: this.selectedReport!.id,
      status: this.reportStatus,
      service: this.service,
      onError: (msg) => this.errorMessage = msg,
      onDownload: () => this.downloadExcel()
    }));
  }
}
