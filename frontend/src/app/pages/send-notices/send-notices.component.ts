import { AfterViewInit, Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

import { DatatableHelper } from '../../shared/datatable/datatable.helper';
import { NoticeType, SendNoticesService, ValidationRow } from './send-notices.service';

declare const $: any;

@Component({
  selector: 'app-send-notices',
  templateUrl: './send-notices.component.html',
  styleUrls: ['./send-notices.component.css']
})
export class SendNoticesComponent implements AfterViewInit {
  noticeTypes: NoticeType[] = [];
  rows: ValidationRow[] = [];
  selectedFile: File | null = null;
  successMessage = '';
  errorMessage = '';
  loading = false;

  readonly form = this.fb.group({
    processSno: ['', Validators.required],
    sendSms: [true],
    sendWhatsapp: [true],
    scheduleDate: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: SendNoticesService,
    private readonly datatableHelper: DatatableHelper
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

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] || null;
  }

  submit(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (this.form.invalid || !this.selectedFile) {
      this.errorMessage = 'Please select notice type and zip file.';
      this.form.markAllAsTouched();
      return;
    }

    const processSno = Number(this.form.value.processSno);
    const sendSms = !!this.form.value.sendSms;
    const sendWhatsapp = !!this.form.value.sendWhatsapp;

    if (!sendSms && !sendWhatsapp) {
      this.errorMessage = 'Please choose SMS and/or WhatsApp.';
      return;
    }

    this.loading = true;
    this.service.scheduleNotice(processSno, sendSms, sendWhatsapp, this.selectedFile).subscribe({
      next: (response) => {
        this.loading = false;
        this.rows = response.rows;
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
    this.service.getNoticeTypes().subscribe({
      next: (types) => {
        this.noticeTypes = types;
      },
      error: () => {
        this.errorMessage = 'Failed to load notice types';
      }
    });
  }
}
