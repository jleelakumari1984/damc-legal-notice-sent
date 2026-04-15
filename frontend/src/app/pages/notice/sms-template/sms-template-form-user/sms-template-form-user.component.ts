import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService } from '../../../../core/services/notice-template.service';
import { SmsTemplate, SmsUserTemplateRequest, NoticeType } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-sms-template-form-user',
  templateUrl: './sms-template-form-user.component.html'
})
export class SmsTemplateFormUserComponent implements OnInit, OnChanges {
  @Input() editTemplate: SmsTemplate | null = null;
  @Input() noticeType: NoticeType | null = null;
  @Output() saved = new EventEmitter<void>();

  form!: FormGroup;
  saving = false;
  errorMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: NoticeTemplateService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      userTemplateContent: ['', [Validators.required]],
      status: [1]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editTemplate']) {
      const t = changes['editTemplate'].currentValue as SmsTemplate | null;
      if (t) {
        this.form.setValue({
          userTemplateContent: t.userTemplateContent ?? '',
          status: t.status ?? 1
        });
      } else {
        this.form.reset({ userTemplateContent: '', status: 1 });
      }
      this.errorMessage = '';
    }
  }

  open(): void {
    $('#smsUserTemplateModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ userTemplateContent: '', status: 1 });
    this.errorMessage = '';
    $('#smsUserTemplateModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.noticeType) return;
    const v = this.form.value;
    const request: SmsUserTemplateRequest = {
      processId: this.noticeType.id,
      userTemplateContent: v.userTemplateContent.trim(),
      status: v.status ? 1 : 0
    };
    this.saving = true;
    this.errorMessage = '';
    const call$ = this.editTemplate
      ? this.service.updateSmsTemplate(this.editTemplate.id, request)
      : this.service.createSmsTemplate(request);

    call$.subscribe({
      next: () => {
        this.saving = false;
        $('#smsUserTemplateModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Failed to save SMS template.';
      }
    });
  }
}
