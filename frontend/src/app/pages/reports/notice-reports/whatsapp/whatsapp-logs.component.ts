import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NoticeReportSummary } from '../../../../core/models/report.notice';
import { DatatableHelper } from '../../../../shared/datatable/datatable.helper';
import { WhatsappLogsDatatable } from '../../../../shared/datatable/whatsapp-logs-datatable';
import { NoticeReportsService } from '../../../../core/services/notice-reports.service';

@Component({
  selector: 'app-whatsapp-logs',
  templateUrl: './whatsapp-logs.component.html'
})
export class WhatsappLogsComponent implements OnInit, AfterViewInit {
  @Input() selectedReport: NoticeReportSummary | null = null;
  @Input() reportStatus: string = '';
  @Output() onClose = new EventEmitter<void>();
  activeTab: 'SUCCESS' | 'ERROR' = 'SUCCESS';
  downloading = false;
  errorMessage = '';

  constructor(private readonly datatableHelper: DatatableHelper, private readonly service: NoticeReportsService) { }

  ngOnInit(): void { }

  ngAfterViewInit(): void {
    if (this.selectedReport) {
      this.initTable();
      setTimeout(() => document.getElementById('whatsappDetailSection')?.scrollIntoView({ behavior: 'smooth' }), 100);
    }
  }

  private initTable(): void {
    this.datatableHelper.initTable('#whatsappLogsTable', new WhatsappLogsDatatable({
      reportId: this.selectedReport!.id,
      status: this.reportStatus,
      service: this.service,
      onError: (msg) => this.errorMessage = msg,
      onDownload: () => this.downloadExcel()
    }));
  }
  switchTab(tab: 'SUCCESS' | 'ERROR'): void {
    if (this.activeTab === tab) return;
    this.activeTab = tab;
    this.reportStatus = tab.toLowerCase();
    this.errorMessage = '';
    this.datatableHelper.destroy('#whatsappLogsTable');
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
    const source$ = this.service.exportWhatsappLogs(this.selectedReport.id, request);
    source$.subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `whatsapp-logs-${this.selectedReport!.id}-${this.activeTab.toLowerCase()}.csv`;
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
}
