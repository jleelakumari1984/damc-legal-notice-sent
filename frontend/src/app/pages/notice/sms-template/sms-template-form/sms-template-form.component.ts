import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeTemplateService } from '../../../../core/services/notice-template.service';
import { SmsTemplate, SmsTemplateRequest, NoticeType } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
    selector: 'app-sms-template-form',
    templateUrl: './sms-template-form.component.html'
})
export class SmsTemplateFormComponent implements OnInit, OnChanges {
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
            peid: ['', [Validators.maxLength(100)]],
            senderId: ['', [Validators.required, Validators.maxLength(50)]],
            routeId: ['', [Validators.maxLength(50)]],
            templateContent: ['', [Validators.required]],
            templateId: ['', [Validators.required, Validators.maxLength(100)]],
            channel: ['', [Validators.maxLength(50)]],
            dcs: [0],
            flashSms: [0],
            status: [1]
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (!this.form) return;
        if (changes['editTemplate']) {
            const t = changes['editTemplate'].currentValue as SmsTemplate | null;
            if (t) {
                this.form.setValue({
                    peid: t.peid ?? '',
                    senderId: t.senderId ?? '',
                    routeId: t.routeId ?? '',
                    templateContent: t.templateContent ?? '',
                    templateId: t.templateId ?? '',
                    channel: t.channel ?? '',
                    dcs: t.dcs ?? 0,
                    flashSms: t.flashSms ?? 0,
                    status: t.status ?? 1
                });
            } else {
                this.form.reset({
                    peid: '', senderId: '', routeId: '', templateContent: '',
                    templateId: '', channel: '', dcs: 0, flashSms: 0, status: 1
                });
            }
            this.errorMessage = '';
        }
    }

    open(): void {
        $('#smsTemplateModal').modal('show');
    }

    cancel(): void {
        this.form.reset({
            peid: '', senderId: '', routeId: '', templateContent: '',
            templateId: '', channel: '', dcs: 0, flashSms: 0, status: 1
        });
        this.errorMessage = '';
        $('#smsTemplateModal').modal('hide');
    }

    submit(): void {
        if (this.form.invalid || !this.noticeType) return;
        const v = this.form.value;
        const request: SmsTemplateRequest = {
            processId: this.noticeType.id,
            peid: v.peid?.trim() ?? '',
            senderId: v.senderId.trim(),
            routeId: v.routeId?.trim() ?? '',
            templateContent: v.templateContent.trim(),
            userTemplateContent: v.templateContent.trim(),
            templateId: v.templateId.trim(),
            channel: v.channel?.trim() ?? '',
            dcs: v.dcs ? 1 : 0,
            flashSms: v.flashSms ? 1 : 0,
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
                $('#smsTemplateModal').modal('hide');
                this.saved.emit();
            },
            error: () => {
                this.saving = false;
                this.errorMessage = 'Failed to save SMS template.';
            }
        });
    }
}
