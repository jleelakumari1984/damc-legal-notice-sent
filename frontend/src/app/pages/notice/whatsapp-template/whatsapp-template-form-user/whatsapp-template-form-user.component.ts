import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService } from '../../../../core/services/notice-template.service';
import { WhatsappTemplate, WhatsappUserTemplateRequest, NoticeType, TemplateApprovedStatus } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-whatsapp-template-form-user',
  templateUrl: './whatsapp-template-form-user.component.html'
})
export class WhatsappTemplateFormUserComponent implements OnInit, OnChanges {
  @Input() editTemplate: WhatsappTemplate | null = null;
  @Input() noticeType: NoticeType | null = null;
  @Output() saved = new EventEmitter<void>();

  form!: FormGroup;
  saving = false;
  errorMessage = '';
  submittedForApproval = false;

  get isApproved(): boolean {
    return this.editTemplate?.approveStatus === TemplateApprovedStatus.APPROVED;
  }

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: NoticeTemplateService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      userTemplateContent: ['', [Validators.required]]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editTemplate']) {
      const t = changes['editTemplate'].currentValue as WhatsappTemplate | null;
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
    $('#whatsappUserTemplateModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ userTemplateContent: '' });
    this.errorMessage = '';
    this.submittedForApproval = false;
    $('#whatsappUserTemplateModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.noticeType || this.isApproved) return;
    const v = this.form.value;
    const request: WhatsappUserTemplateRequest = {
      processId: this.noticeType.id,
      userTemplateContent: v.userTemplateContent.trim(),
      status: 0
    };
    this.saving = true;
    this.errorMessage = '';
    const call$ = this.editTemplate
      ? this.service.updateWhatsappTemplate(this.editTemplate.id, request)
      : this.service.createWhatsappTemplate(request);

    call$.subscribe({
      next: () => {
        this.saving = false;
        this.submittedForApproval = true;
        this.saved.emit();
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Failed to save WhatsApp template.';
      }
    });
  }
}
