import { Component } from '@angular/core';

import { WhatsappTemplate } from '../../../../core/models/notices.model';
import { StorageService } from '../../../../core/services/storage.service';

declare const $: any;

@Component({
    selector: 'app-whatsapp-template-view',
    templateUrl: './whatsapp-template-view.component.html'
})
export class WhatsappTemplateViewComponent {
    viewTemplate: WhatsappTemplate | null = null;

    constructor(
        private readonly storageService: StorageService,
    ) { }

    get isSuperAdmin(): boolean {
        return this.storageService.isSuperAdmin();
    }
    open(template: WhatsappTemplate): void {
        this.viewTemplate = template;
        $('#whatsappViewModal').modal('show');
    }

    close(): void {
        $('#whatsappViewModal').modal('hide');
        this.viewTemplate = null;
    }
}
