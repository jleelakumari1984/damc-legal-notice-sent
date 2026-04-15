import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService } from '../../../../core/services/notice-template.service';
import { WhatsappTemplate, WhatsappTemplateRequest, NoticeType } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-whatsapp-template-form',
  templateUrl: './whatsapp-template-form.component.html'
})
export class WhatsappTemplateFormComponent implements OnInit, OnChanges {
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
      templateName: ['', [Validators.required, Validators.maxLength(255)]],
      templatePath: [''],
      templateContent: ['', [Validators.required]],
      templateLang: ['en', [Validators.required, Validators.maxLength(20)]],
      status: [1]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editTemplate']) {
      const t = changes['editTemplate'].currentValue as WhatsappTemplate | null;
      if (t) {
        this.form.setValue({
          templateName: t.templateName ?? '',
          templatePath: t.templatePath ?? '',
          templateContent: t.templateContent ?? '',
          templateLang: t.templateLang ?? 'en',
          status: t.status ?? 1
        });
      } else {
        this.form.reset({
          templateName: '', templatePath: '',
          templateContent: '', templateLang: 'en', status: 1
        });
      }
      this.errorMessage = '';
    }
  }

  open(): void {
    $('#whatsappTemplateModal').modal('show');
  }

  cancel(): void {
    this.form.reset({
      templateName: '', templatePath: '',
      templateContent: '', templateLang: 'en', status: 1
    });
    this.errorMessage = '';
    $('#whatsappTemplateModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.noticeType) return;
    const v = this.form.value;
    const request: WhatsappTemplateRequest = {
      processId: this.noticeType.id,
      templateName: v.templateName.trim(),
      templateContent: v.templateContent.trim(),
      userTemplateContent: v.templateContent.trim(),
      templateLang: v.templateLang.trim(),
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
        $('#whatsappTemplateModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Failed to save WhatsApp template.';
      }
    });
  }
}
