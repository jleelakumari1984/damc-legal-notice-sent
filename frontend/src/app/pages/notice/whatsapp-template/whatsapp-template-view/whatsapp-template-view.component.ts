import { Component } from '@angular/core';

import { WhatsappTemplate } from '../../../../core/models/notices.model';
import { StorageService } from '../../../../core/services/storage.service';
import { BaseComponent } from '../../../../shared/base/base.component';

declare const $: any;

@Component({
    selector: 'app-whatsapp-template-view',
    templateUrl: './whatsapp-template-view.component.html'
})
export class WhatsappTemplateViewComponent extends BaseComponent {
    viewTemplate: WhatsappTemplate | null = null;

    constructor(
    ) { super(); }


    open(template: WhatsappTemplate): void {
        this.viewTemplate = template;
        $('#whatsappViewModal').modal('show');
    }

    close(): void {
        $('#whatsappViewModal').modal('hide');
        this.viewTemplate = null;
    }
}
