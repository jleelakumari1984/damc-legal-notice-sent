import { Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService } from '../../../../core/services/notice-template.service';
import { NoticeExcelMappingsService } from '../../../../core/services/notice-excel-mappings.service';
import { SmsTemplate, SmsUserTemplateRequest, NoticeType, TemplateApprovedStatus } from '../../../../core/models/notices.model';
import { NoticeExcelMappingResponse } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-sms-template-form-user',
  templateUrl: './sms-template-form-user.component.html'
})
export class SmsTemplateFormUserComponent implements OnInit, OnChanges {
  @Input() editTemplate: SmsTemplate | null = null;
  @Input() noticeType: NoticeType | null = null;
  @Output() saved = new EventEmitter<SmsTemplate>();

  @ViewChild('contentArea') contentArea!: ElementRef<HTMLTextAreaElement>;

  form!: FormGroup;
  saving = false;
  errorMessage = '';
  submittedForApproval = false;
  excelFields: NoticeExcelMappingResponse[] = [];

  get isApproved(): boolean {
    return this.editTemplate?.approveStatus === TemplateApprovedStatus.APPROVED;
  }

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: NoticeTemplateService,
    private readonly excelMappingsService: NoticeExcelMappingsService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      userTemplateContent: ['', [Validators.required]]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editTemplate']) {
      const t = changes['editTemplate'].currentValue as SmsTemplate | null;
      if (t) {
        this.form.setValue({ userTemplateContent: t.userTemplateContent ?? '' });
      } else {
        this.form.reset({ userTemplateContent: '' });
      }
      this.errorMessage = '';
      this.submittedForApproval = false;
    }
  }

  open(): void {
    this.submittedForApproval = false;
    if (this.noticeType) {
      this.excelMappingsService.getByNoticeId(this.noticeType.id).subscribe({
        next: (fields) => { this.excelFields = fields; },
        error: () => { this.excelFields = []; }
      });
    }
    $('#smsUserTemplateModal').modal('show');
  }

  insertField(fieldName: string): void {
    const tag = `{{${fieldName}}}`;
    const el = this.contentArea?.nativeElement;
    if (!el) {
      const current = this.form.get('userTemplateContent')?.value ?? '';
      this.form.get('userTemplateContent')?.setValue(current + tag);
      return;
    }
    const start = el.selectionStart ?? el.value.length;
    const end = el.selectionEnd ?? el.value.length;
    const newValue = el.value.substring(0, start) + tag + el.value.substring(end);
    this.form.get('userTemplateContent')?.setValue(newValue);
    setTimeout(() => {
      el.selectionStart = start + tag.length;
      el.selectionEnd = start + tag.length;
      el.focus();
    });
  }

  cancel(): void {
    this.form.reset({ userTemplateContent: '' });
    this.errorMessage = '';
    this.submittedForApproval = false;
    $('#smsUserTemplateModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.noticeType || this.isApproved) return;
    const v = this.form.value;
    const request: SmsUserTemplateRequest = {
      noticeId: this.noticeType.id,
      userTemplateContent: v.userTemplateContent.trim(),
      status: 0
    };
    this.saving = true;
    this.errorMessage = '';
    const call$ = this.editTemplate
      ? this.service.updateSmsTemplate(this.editTemplate.id, request)
      : this.service.createSmsTemplate(request);

    call$.subscribe({
      next: (data: SmsTemplate) => {
        this.editTemplate = data;
        this.saving = false;
        this.submittedForApproval = true;
        this.saved.emit(data);
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Failed to save SMS template.';
      }
    });
  }
} 