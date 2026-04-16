import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService, ApproveTemplateRequest, RejectTemplateRequest } from '../../../core/services/notice-template.service';
import { NoticeExcelMappingsService } from '../../../core/services/notice-excel-mappings.service';
import { SmsPendingTemplate, SmsTemplate, NoticeExcelMappingResponse } from '../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-sms-approval-form',
  templateUrl: './sms-approval-form.component.html'
})
export class SmsApprovalFormComponent {
  @Input() inline = false;
  @Output() done = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  @ViewChild('approveContentArea') approveContentArea!: ElementRef<HTMLTextAreaElement>;

  template: SmsPendingTemplate | null = null;
  isReject = false;
  saving = false;
  errorMessage = '';
  excelFields: NoticeExcelMappingResponse[] = [];

  approveForm!: FormGroup;
  rejectForm!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: NoticeTemplateService,
    private readonly excelMappingsService: NoticeExcelMappingsService
  ) {
    this.approveForm = this.fb.group({
      peid: [''],
      senderId: ['', Validators.required],
      routeId: [''],
      templateContent: ['', Validators.required],
      templateId: ['', Validators.required],
      channel: [''],
      dcs: [0],
      flashSms: [0]
    });
    this.rejectForm = this.fb.group({
      rejectionReason: ['', Validators.required]
    });
  }

  open(template: SmsPendingTemplate, isReject: boolean, isInline: boolean): void {
    this._load(template, isReject);
    this.inline = isInline;
    if (!isInline) {
      $('#smsApprovalModal').modal('show');
    }
  }
  private _load(template: SmsPendingTemplate, isReject: boolean): void {
    this.template = template;
    this.isReject = isReject;
    this.errorMessage = '';
    this.approveForm.reset({ dcs: 0, flashSms: 0 });
    this.rejectForm.reset();
    //console.log('Template noticeId:', template.noticeId);
    if (template.noticeId) {
      this.excelMappingsService.getByNoticeId(template.noticeId).subscribe({
        next: (fields) => { this.excelFields = fields; },
        error: () => { this.excelFields = []; }
      });
    } else {
      this.excelFields = [];
    }
  }

  insertField(fieldName: string): void {
    const tag = `{{${fieldName}}}`;
    const el = this.approveContentArea?.nativeElement;
    if (!el) {
      const current = this.approveForm.get('templateContent')?.value ?? '';
      this.approveForm.get('templateContent')?.setValue(current + tag);
      return;
    }
    const start = el.selectionStart ?? el.value.length;
    const end = el.selectionEnd ?? el.value.length;
    const newValue = el.value.substring(0, start) + tag + el.value.substring(end);
    this.approveForm.get('templateContent')?.setValue(newValue);
    setTimeout(() => {
      el.selectionStart = start + tag.length;
      el.selectionEnd = start + tag.length;
      el.focus();
    });
  }

  cancel(): void {
    if (this.inline) {
      this.cancelled.emit();
    } else {
      $('#smsApprovalModal').modal('hide');
    }
  }

  submit(): void {
    if (!this.template) return;
    this.saving = true;
    this.errorMessage = '';

    if (this.isReject) {
      if (this.rejectForm.invalid) { this.saving = false; return; }
      const req: RejectTemplateRequest = { rejectionReason: this.rejectForm.value.rejectionReason.trim() };
      this.service.rejectSmsTemplate(this.template.id, req).subscribe({
        next: () => { this.saving = false; if (!this.inline) { $('#smsApprovalModal').modal('hide'); } this.done.emit(); },
        error: () => { this.saving = false; this.errorMessage = 'Failed to reject template.'; }
      });
    } else {
      if (this.approveForm.invalid) { this.saving = false; return; }
      const v = this.approveForm.value;
      const req: ApproveTemplateRequest = {
        peid: v.peid?.trim() || undefined,
        senderId: v.senderId.trim(),
        routeId: v.routeId?.trim() || undefined,
        templateContent: v.templateContent.trim(),
        templateId: v.templateId.trim(),
        channel: v.channel?.trim() || undefined,
        dcs: v.dcs,
        flashSms: v.flashSms
      };
      this.service.approveSmsTemplate(this.template.id, req).subscribe({
        next: () => { this.saving = false; if (!this.inline) { $('#smsApprovalModal').modal('hide'); } this.done.emit(); },
        error: () => { this.saving = false; this.errorMessage = 'Failed to approve template.'; }
      });
    }
  }
}
