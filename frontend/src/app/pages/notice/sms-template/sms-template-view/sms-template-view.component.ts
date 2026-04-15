import { Component } from '@angular/core';

import { SmsTemplate } from '../../../../core/models/notices.model';
import { StorageService } from '../../../../core/services/storage.service';

declare const $: any;

@Component({
    selector: 'app-sms-template-view',
    templateUrl: './sms-template-view.component.html'
})
export class SmsTemplateViewComponent {
    viewTemplate: SmsTemplate | null = null;

    constructor(
        private readonly storageService: StorageService,
    ) { }

    get isSuperAdmin(): boolean {
        return this.storageService.isSuperAdmin();
    }
    open(template: SmsTemplate): void {
        this.viewTemplate = template;
        $('#smsViewModal').modal('show');
    }

    close(): void {
        $('#smsViewModal').modal('hide');
        this.viewTemplate = null;
    }
}
