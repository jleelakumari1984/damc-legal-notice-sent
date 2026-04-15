import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService, ApproveTemplateRequest, RejectTemplateRequest } from '../../../core/services/notice-template.service';
import { WhatsappPendingTemplate, WhatsappTemplate } from '../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-whatsapp-approval-form',
  templateUrl: './whatsapp-approval-form.component.html'
})
export class WhatsappApprovalFormComponent {
  @Output() done = new EventEmitter<void>();

  template: WhatsappPendingTemplate | null = null;
  isReject = false;
  saving = false;
  errorMessage = '';

  approveForm!: FormGroup;
  rejectForm!: FormGroup;

  constructor(private readonly fb: FormBuilder, private readonly service: NoticeTemplateService) {
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

  open(template: WhatsappPendingTemplate, isReject: boolean): void {
    this.template = template;
    this.isReject = isReject;
    this.errorMessage = '';
    this.approveForm.reset({ templateLang: 'en' });
    this.rejectForm.reset();
    $('#whatsappApprovalModal').modal('show');
  }

  cancel(): void {
    $('#whatsappApprovalModal').modal('hide');
  }

  submit(): void {
    if (!this.template) return;
    this.saving = true;
    this.errorMessage = '';

    if (this.isReject) {
      if (this.rejectForm.invalid) { this.saving = false; return; }
      const req: RejectTemplateRequest = { rejectionReason: this.rejectForm.value.rejectionReason.trim() };
      this.service.rejectWhatsappTemplate(this.template.id, req).subscribe({
        next: () => { this.saving = false; $('#whatsappApprovalModal').modal('hide'); this.done.emit(); },
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
        next: () => { this.saving = false; $('#whatsappApprovalModal').modal('hide'); this.done.emit(); },
        error: () => { this.saving = false; this.errorMessage = 'Failed to approve template.'; }
      });
    }
  }
}
