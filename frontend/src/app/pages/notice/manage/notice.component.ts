import { AfterViewInit, Component, ViewChild } from '@angular/core';

import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { NoticesDatatable } from '../../../shared/datatable/notices-datatable';
import { NoticeService } from '../../../core/services/notice.service';
import { NoticeFormComponent } from './notice-form/notice-form.component';
import { Notice, NoticeType } from '../../../core/models/notices.model';
import { NoticeExcelMappingsComponent } from '../excel-mappings/notice-excel-mappings.component';

@Component({
  selector: 'app-notice',
  templateUrl: './notice.component.html',
  styleUrls: ['./notice.component.css']
})
export class NoticeComponent implements AfterViewInit {
  @ViewChild(NoticeFormComponent) noticeForm!: NoticeFormComponent;
  @ViewChild(NoticeExcelMappingsComponent) noticeExcelMappings!: NoticeExcelMappingsComponent;

  editNotice: NoticeType | null = null;
  actionType: string = 'display';
  deletingId: number | null = null;
  actionId: number | null = null;

  private readonly tableId = "#noticesTable";
  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly service: NoticeService,
    private readonly datatableHelper: DatatableHelper
  ) { }

  ngAfterViewInit(): void {
    this.initTable();
  }

  private initTable(): void {
    this.datatableHelper.initTable(this.tableId, new NoticesDatatable({
      service: this.service,
      callbacks: {
        onSmsConfig: (notice) => this.openSmsConfig(notice),
        onWhatsappConfig: (notice) => this.openWhatsappConfig(notice),
        onExcelConfig: (notice) => this.openExcelConfig(notice),
        onError: (msg) => this.errorMessage = msg
      }
    }));
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

  openSmsConfig(notice: NoticeType): void {
    this.actionType = 'sms';
    this.editNotice = notice;
    this.clearMessages();
  }

  onSaved(): void {
    this.successMessage = this.editNotice ? 'Notice updated.' : 'Notice created.';
    this.editNotice = null;
    this.datatableHelper.reload(this.tableId);
  }

  openWhatsappConfig(notice: NoticeType): void {
    this.actionType = 'whatsapp';
    this.editNotice = notice;
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
