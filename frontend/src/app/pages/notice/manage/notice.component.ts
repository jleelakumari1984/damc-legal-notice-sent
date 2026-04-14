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
    this.datatableHelper.initTable('#noticesTable', new NoticesDatatable({
      onSmsConfig: (notice) => this.openSmsConfig(notice),
      onWhatsappConfig: (notice) => this.openWhatsappConfig(notice),
      onExcelConfig: (notice) => this.openExcelConfig(notice),
      onError: (msg) => this.errorMessage = msg
    }, this.service));
  }

  reload(): void {
    this.clearMessages();
    this.datatableHelper.reload('#noticesTable');
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
    this.noticeForm.open();
  }

  onSaved(): void {
    this.successMessage = this.editNotice ? 'Notice updated.' : 'Notice created.';
    this.editNotice = null;
    this.datatableHelper.reload('#noticesTable');
  }

  openWhatsappConfig(notice: NoticeType): void {
    this.actionType = 'whatsapp';
    this.editNotice = notice;
    this.clearMessages();
  }

  openExcelConfig(notice: NoticeType): void {
    this.actionType = 'excel';
    this.editNotice = notice;
    this.clearMessages();

  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
