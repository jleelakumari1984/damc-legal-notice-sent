import { AfterViewInit, Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

import { DatatableHelper } from '../../../shared/datatable/datatable.helper';

import { NoticeService } from '../../../core/services/notice.service';
import { SendNoticesService } from '../../../core/services/send-notices.service';
import { NoticeType } from '../../../core/models/notices.model';
import { ExcelPreview, ExcelPreviewRow, NoticeFileData, ValidationRow } from '../../../core/models/excel.model';
import { SendSampleRequest } from '../../../core/models/schedule.model';
import { NoticeReportFilter } from '../../../core/models/report.notice';

declare const $: any;

@Component({
  selector: 'app-send-notices',
  templateUrl: './send-notices.component.html',
  styleUrls: ['./send-notices.component.css']
})
export class SendNoticesComponent implements AfterViewInit {
  noticeTypes: NoticeType[] = [];
  selectedNoticeType: NoticeType | null = null;
  rows: ValidationRow[] = [];
  selectedFile: File | null = null;
  successMessage = '';
  errorMessage = '';
  loading = false;
  previewLoading = false;
  preview: ExcelPreview | null = null;
  validationColumns: string[] = [];
  selectedPreviewRow: ExcelPreviewRow | null = null;
  sampleMobile = '';
  sampleSendSms = true;
  sampleSendWhatsapp = true;
  sampleLoading = false;
  sampleSuccess = '';
  sampleError = '';

  parseExcelData(json: string): Record<string, unknown> {
    try {
      const parsed = JSON.parse(json);
      return (parsed?.data ?? parsed) as Record<string, unknown>;
    } catch {
      return {};
    }
  }

  readonly form = this.fb.group({
    noticeSno: ['', Validators.required],
    sendSms: [true],
    sendWhatsapp: [true],
    scheduleDate: [new Date().toISOString().split('T')[0]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: SendNoticesService,
    private readonly datatableHelper: DatatableHelper,
    private readonly noticeService: NoticeService
  ) {
    this.loadNoticeTypes();
  }

  ngAfterViewInit(): void {
    $('.datepicker').datepicker({
      format: 'yyyy-mm-dd',
      autoclose: true,
      todayHighlight: true
    });
  }

  onNoticeTypeChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.selectedNoticeType = this.noticeTypes.find(t => t.id === id) ?? null;
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] || null;
    const name = file?.name.toLowerCase() ?? '';
    if (file && !name.endsWith('.zip') && !name.endsWith('.xlsx') && !name.endsWith('.xls')) {
      this.errorMessage = 'Only ZIP and Excel files (.zip, .xlsx, .xls) are allowed.';
      input.value = '';
      this.selectedFile = null;
      this.preview = null;
      return;
    }
    this.errorMessage = '';
    this.selectedFile = file;
    this.preview = null;
    if (this.selectedFile) {
      this.previewLoading = true;
      this.service.previewExcel(this.selectedFile).subscribe({
        next: (data) => {
          this.preview = data;
          this.previewLoading = false;
          this.selectedPreviewRow = null;
          this.sampleMobile = '';
          this.sampleSuccess = '';
          this.sampleError = '';
          setTimeout(() => this.datatableHelper.init('#previewDataTable'), 100);
        },
        error: () => {
          this.previewLoading = false;
        }
      });
    }
  }

  sendSample(): void {
    if (!this.selectedPreviewRow || !this.selectedNoticeType || !this.sampleMobile.trim()) {
      this.sampleError = 'Please select a row and enter a mobile number.';
      return;
    }
    if (!this.sampleSendSms && !this.sampleSendWhatsapp) {
      this.sampleError = 'Please choose SMS and/or WhatsApp.';
      return;
    }
    this.sampleLoading = true;
    this.sampleSuccess = '';
    this.sampleError = '';
    const request: SendSampleRequest = {
      noticeSno: this.selectedNoticeType.id,
      mobileNumber: this.sampleMobile.trim(),
      sendSms: this.sampleSendSms,
      sendWhatsapp: this.sampleSendWhatsapp,
      rowData: this.selectedPreviewRow.data
    };
    this.service.sendSample(request).subscribe({
      next: () => {
        this.sampleLoading = false;
        this.sampleSuccess = 'Sample sent successfully to ' + this.sampleMobile.trim();
      },
      error: (err) => {
        this.sampleLoading = false;
        this.sampleError = err?.error?.error || 'Failed to send sample.';
      }
    });
  }

  submit(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (this.form.invalid || !this.selectedFile) {
      this.errorMessage = 'Please select notice type and zip file.';
      this.form.markAllAsTouched();
      return;
    }

    const noticeSno = Number(this.form.value.noticeSno);
    const sendSms = !!this.form.value.sendSms;
    const sendWhatsapp = !!this.form.value.sendWhatsapp;

    if (!sendSms && !sendWhatsapp) {
      this.errorMessage = 'Please choose SMS and/or WhatsApp.';
      return;
    }

    this.loading = true;
    this.service.scheduleNotice(noticeSno, sendSms, sendWhatsapp, this.selectedFile).subscribe({
      next: (response) => {
        this.loading = false;
        const fileData: NoticeFileData = response.fileData ?? { columnNames: [], rows: [] };
        this.rows = fileData.rows;
        this.validationColumns = fileData.columnNames;
        this.successMessage = `Scheduled successfully with ID ${response.scheduleId}`;
        setTimeout(() => this.datatableHelper.init('#validationTable'), 100);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error?.error?.error || 'Failed to schedule notices';
      }
    });
  }

  private loadNoticeTypes(): void {
    this.noticeService.getNoticeTypes().subscribe({
      next: (types) => {
        this.noticeTypes = types.data;
      },
      error: () => {
        this.errorMessage = 'Failed to load notice types';
      }
    });
  }
}
