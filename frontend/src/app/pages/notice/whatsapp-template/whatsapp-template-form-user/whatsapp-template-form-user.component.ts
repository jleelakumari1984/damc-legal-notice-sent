import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService } from '../../../../core/services/notice-template.service';
import { WhatsappTemplate, WhatsappUserTemplateRequest, NoticeType } from '../../../../core/models/notices.model';

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
      const t = changes['editTemplate'].currentValue as WhatsappTemplate | null;
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
    $('#whatsappUserTemplateModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ userTemplateContent: '', status: 1 });
    this.errorMessage = '';
    $('#whatsappUserTemplateModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.noticeType) return;
    const v = this.form.value;
    const request: WhatsappUserTemplateRequest = {
      processId: this.noticeType.id,
      userTemplateContent: v.userTemplateContent.trim(),
      status: v.status ? 1 : 0
    };
    this.saving = true;
    this.errorMessage = '';
    const call$ = this.editTemplate
      ? this.service.updateWhatsappTemplate(this.editTemplate.id, request)
      : this.service.createWhatsappTemplate(request);

    call$.subscribe({
      next: () => {
        this.saving = false;
        $('#whatsappUserTemplateModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Failed to save WhatsApp template.';
      }
    });
  }
}
