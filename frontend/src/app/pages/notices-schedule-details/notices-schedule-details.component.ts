import { AfterViewInit, Component, OnInit } from '@angular/core';

import { DatatableHelper } from '../../shared/datatable/datatable.helper';
import {
  ScheduledNotice,
  ScheduledNoticeDetail,
  SendNoticesService
} from '../../core/services/send-notices.service';

declare const $: any;

@Component({
  selector: 'app-notices-schedule-details',
  templateUrl: './notices-schedule-details.component.html',
  styleUrls: ['./notices-schedule-details.component.css']
})
export class NoticesScheduleDetailsComponent implements OnInit, AfterViewInit {
  notices: ScheduledNotice[] = [];
  selectedNotice: ScheduledNoticeDetail | null = null;
  loadingList = false;
  loadingDetail = false;
  errorMessage = '';
  detailError = '';
  itemColumns: string[] = [];

  constructor(
    private readonly service: SendNoticesService,
    private readonly datatableHelper: DatatableHelper
  ) {}

  ngOnInit(): void {
    this.loadNotices();
  }

  ngAfterViewInit(): void {}

  loadNotices(): void {
    this.loadingList = true;
    this.errorMessage = '';
    this.service.getScheduledNotices().subscribe({
      next: (data) => {
        this.notices = data.sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.loadingList = false;
        setTimeout(() => this.datatableHelper.init('#noticesTable'), 100);
      },
      error: () => {
        this.loadingList = false;
        this.errorMessage = 'Failed to load scheduled notices.';
      }
    });
  }

  viewDetail(id: number): void {
    this.loadingDetail = true;
    this.detailError = '';
    this.selectedNotice = null;
    this.service.getScheduledNoticeDetail(id).subscribe({
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

  statusBadge(status: string): string {
    switch (status?.toUpperCase()) {
      case 'SUCCESS': return 'bg-success';
      case 'FAILED': return 'bg-danger';
      case 'PENDING': return 'bg-warning text-dark';
      case 'PROCESSING': return 'bg-info text-dark';
      default: return 'bg-secondary';
    }
  }

  objectKeys(obj: Record<string, unknown>): string[] {
    return obj ? Object.keys(obj) : [];
  }
}
