import { Component } from '@angular/core';

import { SmsTemplate } from '../../../../core/models/notices.model';
import { StorageService } from '../../../../core/services/storage.service';
import { BaseComponent } from '../../../../shared/base/base.component';

declare const $: any;

@Component({
    selector: 'app-sms-template-view',
    templateUrl: './sms-template-view.component.html'
})
export class SmsTemplateViewComponent extends BaseComponent {
    viewTemplate: SmsTemplate | null = null;

    constructor(

    ) { super(); }

  
    open(template: SmsTemplate): void {
        this.viewTemplate = template;
        $('#smsViewModal').modal('show');
    }

    close(): void {
        $('#smsViewModal').modal('hide');
        this.viewTemplate = null;
    }
}
