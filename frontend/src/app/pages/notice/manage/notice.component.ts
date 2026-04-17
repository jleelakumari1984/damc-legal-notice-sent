import { AfterViewInit, Component, ViewChild } from '@angular/core';

import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { NoticesDatatable } from '../../../shared/datatable/notices-datatable';
import { NoticeService } from '../../../core/services/notice.service';
import { NoticeFormComponent } from './notice-form/notice-form.component';
import { NoticeFilterComponent } from './notice-filter/notice-filter.component';
import { NoticeType } from '../../../core/models/notices.model';
import { NoticeExcelMappingsComponent } from '../excel-mappings/notice-excel-mappings.component';
import { StorageService } from '../../../core/services/storage.service';
import { NoticeReportFilter } from '../../../core/models/report.notice';

@Component({
  selector: 'app-notice',
  templateUrl: './notice.component.html',
  styleUrls: ['./notice.component.css']
})
export class NoticeComponent implements AfterViewInit {
  @ViewChild(NoticeFormComponent) noticeForm!: NoticeFormComponent;
  @ViewChild(NoticeExcelMappingsComponent) noticeExcelMappings!: NoticeExcelMappingsComponent;
  @ViewChild(NoticeFilterComponent) noticeFilter!: NoticeFilterComponent;

  editNotice: NoticeType | null = null;
  actionType: string = 'display';
  deletingId: number | null = null;
  actionId: number | null = null;
  isActiveStatus: boolean | null = null;
  private readonly tableId = "#noticesTable";
  private activeFilter: NoticeReportFilter = {};
  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly service: NoticeService,
    private readonly datatableHelper: DatatableHelper,
    private readonly storageService: StorageService
  ) {
    this.activeFilter.userId = this.storageService.getUser()?.id;
  }

  ngAfterViewInit(): void {
    this.initTable();
  }

  private initTable(): void {
    this.datatableHelper.initTable(this.tableId, new NoticesDatatable({
      service: this.service,
      storageService: this.storageService,
      getFilters: () => this.activeFilter,
      callbacks: {
        onSmsConfig: (notice, active) => this.openSmsConfig(notice, active),
        onWhatsappConfig: (notice, active) => this.openWhatsappConfig(notice, active),
        onExcelConfig: (notice) => this.openExcelConfig(notice),
        onError: (msg) => this.errorMessage = msg
      }
    }));
  }

  onFilterChange(filter: NoticeReportFilter): void {
    this.activeFilter = filter;
    this.datatableHelper.reload(this.tableId);
  }

  reload(): void {
    this.clearMessages();
    this.datatableHelper.reload(this.tableId);
  }

  openAddModal(): void {
    this.editNotice = null;
    this.clearMessages();
    this.noticeForm.open();
  }

  openSmsConfig(notice: NoticeType, isActiveStatus: boolean): void {
    this.actionType = 'sms';
    this.editNotice = notice;
    this.isActiveStatus = isActiveStatus;
    this.clearMessages();
  }

  onSaved(): void {
    this.successMessage = this.editNotice ? 'Notice updated.' : 'Notice created.';
    this.editNotice = null;
    this.datatableHelper.reload(this.tableId);
  }

  openWhatsappConfig(notice: NoticeType, isActiveStatus: boolean): void {
    this.actionType = 'whatsapp';
    this.editNotice = notice;
    this.isActiveStatus = isActiveStatus;
    this.clearMessages();
  }

  closeSmsTemplate($event: boolean): void {
    this.editNotice = null;
    this.actionType = 'display';
    if ($event) {
      this.datatableHelper.reload(this.tableId);
    }
  }

  closeWhatsappTemplate($event: boolean): void {
    this.editNotice = null;
    this.actionType = 'display';
    if ($event) {
      this.datatableHelper.reload(this.tableId);
    }
  }

  openExcelConfig(notice: NoticeType): void {
    this.actionType = 'excel';
    this.editNotice = notice;
    this.clearMessages();

  }
  closeNoticeMapping(): void {
    this.editNotice = null;
    this.actionType = 'display';
  }
  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
