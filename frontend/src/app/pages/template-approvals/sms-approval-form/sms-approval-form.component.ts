import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService, ApproveTemplateRequest, RejectTemplateRequest } from '../../../core/services/notice-template.service';
import { SmsPendingTemplate, SmsTemplate } from '../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-sms-approval-form',
  templateUrl: './sms-approval-form.component.html'
})
export class SmsApprovalFormComponent {
  @Output() done = new EventEmitter<void>();

  template: SmsPendingTemplate | null = null;
  isReject = false;
  saving = false;
  errorMessage = '';

  approveForm!: FormGroup;
  rejectForm!: FormGroup;

  constructor(private readonly fb: FormBuilder, private readonly service: NoticeTemplateService) {
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

  open(template: SmsPendingTemplate, isReject: boolean): void {
    this.template = template;
    this.isReject = isReject;
    this.errorMessage = '';
    this.approveForm.reset({ dcs: 0, flashSms: 0 });
    this.rejectForm.reset();
    $('#smsApprovalModal').modal('show');
  }

  cancel(): void {
    $('#smsApprovalModal').modal('hide');
  }

  submit(): void {
    if (!this.template) return;
    this.saving = true;
    this.errorMessage = '';

    if (this.isReject) {
      if (this.rejectForm.invalid) { this.saving = false; return; }
      const req: RejectTemplateRequest = { rejectionReason: this.rejectForm.value.rejectionReason.trim() };
      this.service.rejectSmsTemplate(this.template.id, req).subscribe({
        next: () => { this.saving = false; $('#smsApprovalModal').modal('hide'); this.done.emit(); },
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
        next: () => { this.saving = false; $('#smsApprovalModal').modal('hide'); this.done.emit(); },
        error: () => { this.saving = false; this.errorMessage = 'Failed to approve template.'; }
      });
    }
  }
}
