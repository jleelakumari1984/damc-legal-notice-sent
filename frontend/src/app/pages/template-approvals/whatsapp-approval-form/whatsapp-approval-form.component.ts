import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService, ApproveTemplateRequest, RejectTemplateRequest } from '../../../core/services/notice-template.service';
import { NoticeExcelMappingsService } from '../../../core/services/notice-excel-mappings.service';
import { WhatsappPendingTemplate, NoticeExcelMappingResponse } from '../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-whatsapp-approval-form',
  templateUrl: './whatsapp-approval-form.component.html'
})
export class WhatsappApprovalFormComponent {
  @Input() inline = false;
  @Output() done = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  @ViewChild('approveContentArea') approveContentArea!: ElementRef<HTMLTextAreaElement>;

  template: WhatsappPendingTemplate | null = null;
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
      templateName: ['', Validators.required],
      templateContent: ['', Validators.required],
      templateLang: ['en', Validators.required],
      templatePath: ['']
    });
    this.rejectForm = this.fb.group({
      rejectionReason: ['', Validators.required]
    });
  }

  open(template: WhatsappPendingTemplate, isReject: boolean, isInline: boolean): void {
    this._load(template, isReject);
    this.inline = isInline;
    if (!this.inline) {
      $(`#whatsappApprovalModal`).modal('show');
    }
  }



  private _load(template: WhatsappPendingTemplate, isReject: boolean): void {
    this.template = template;
    this.isReject = isReject;
    this.errorMessage = '';
    this.approveForm.reset({ templateLang: 'en' });
    this.rejectForm.reset();
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
      $(`#whatsappApprovalModal`).modal('hide');
    }
  }

  submit(): void {
    if (!this.template) return;
    this.saving = true;
    this.errorMessage = '';

    if (this.isReject) {
      if (this.rejectForm.invalid) { this.saving = false; return; }
      const req: RejectTemplateRequest = { rejectionReason: this.rejectForm.value.rejectionReason.trim() };
      this.service.rejectWhatsappTemplate(this.template.id, req).subscribe({
        next: () => { this.saving = false; if (!this.inline) { $(`#whatsappApprovalModal`).modal('hide'); } this.done.emit(); },
        error: () => { this.saving = false; this.errorMessage = 'Failed to reject template.'; }
      });
    } else {
      if (this.approveForm.invalid) { this.saving = false; return; }
      const v = this.approveForm.value;
      const req: ApproveTemplateRequest = {
        templateName: v.templateName.trim(),
        templateContent: v.templateContent.trim(),
        templateLang: v.templateLang.trim(),
        templatePath: v.templatePath?.trim() || undefined
      };
      this.service.approveWhatsappTemplate(this.template.id, req).subscribe({
        next: () => { this.saving = false; if (!this.inline) { $(`#whatsappApprovalModal`).modal('hide'); } this.done.emit(); },
        error: () => { this.saving = false; this.errorMessage = 'Failed to approve template.'; }
      });
    }
  }
}
