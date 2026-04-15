import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../../core/services/notice-template.service';
import { WhatsappTemplate, NoticeType } from '../../../core/models/notices.model';
import { WhatsappTemplateFormComponent } from './whatsapp-template-form/whatsapp-template-form.component';
import { WhatsappTemplateFormUserComponent } from './whatsapp-template-form-user/whatsapp-template-form-user.component';
import { WhatsappTemplateViewComponent } from './whatsapp-template-view/whatsapp-template-view.component';
import { StorageService } from '../../../core/services/storage.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { WhatsappTemplatesDatatable } from '../../../shared/datatable/whatsapp-templates-datatable';
import { ConfirmModalService } from '../../../shared/confirm-modal/confirm-modal.service';

@Component({
  selector: 'app-whatsapp-template',
  templateUrl: './whatsapp-template.component.html'
})
export class WhatsappTemplateComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild(WhatsappTemplateFormComponent) adminForm?: WhatsappTemplateFormComponent;
  @ViewChild(WhatsappTemplateFormUserComponent) userForm?: WhatsappTemplateFormUserComponent;
  @ViewChild(WhatsappTemplateViewComponent) viewComp?: WhatsappTemplateViewComponent;

  @Input() selectedNotice: NoticeType | null = null;
  @Output() onClose = new EventEmitter<boolean>();
  changeTemplate = false;
  editTemplate: WhatsappTemplate | null = null;
  successMessage = '';
  errorMessage = '';

  private readonly tableId = '#whatsappTemplatesTable';

  constructor(
    private readonly service: NoticeTemplateService,
    private readonly storageService: StorageService,
    private readonly dtHelper: DatatableHelper,
    private readonly confirmService: ConfirmModalService
  ) { }

  get isSuperAdmin(): boolean {
    return this.storageService.isSuperAdmin();
  }

  ngOnInit(): void {
    if (this.selectedNotice) {
      this.initTable();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedNotice'] && this.selectedNotice) {
      //  this.initTable();
    }
  }

  ngOnDestroy(): void {
    this.dtHelper.destroy(this.tableId);
  }

  private initTable(): void {
    if (!this.selectedNotice) return;
    const dt = new WhatsappTemplatesDatatable({
      processId: this.selectedNotice.id,
      isSuperAdmin: this.isSuperAdmin,
      service: this.service,
      storageService: this.storageService,
      callbacks: {
        onEdit: (t) => this.openEditForm(t),
        onView: (t) => this.viewComp?.open(t),
        onToggle: (t) => this.toggleStatus(t),
        onError: (msg) => { this.errorMessage = msg; }
      }
    });
    setTimeout(() => this.dtHelper.initTable(this.tableId, dt));
  }

  reloadTable(): void {
    this.dtHelper.reload(this.tableId);
  }

  openAddForm(): void {
    this.editTemplate = null;
    this.clearMessages();
    if (this.isSuperAdmin) {
      this.adminForm?.open();
    } else {
      this.userForm?.open();
    }
  }

  openEditForm(template: WhatsappTemplate): void {
    this.editTemplate = template;
    this.clearMessages();
    if (this.isSuperAdmin) {
      this.adminForm?.open();
    } else {
      this.userForm?.open();
    }
  }

  onSaved(): void {
    this.successMessage = this.editTemplate ? 'Template updated.' : 'Template created.';
    this.editTemplate = null;
    this.changeTemplate = true;
    this.reloadTable();
  }

  toggleStatus(template: WhatsappTemplate): void {
    const action = template.status === 1 ? 'disable' : 'enable';
    this.confirmService.confirm({
      title: `${template.status === 1 ? 'Disable' : 'Enable'} Template`,
      message: `Are you sure you want to ${action} this WhatsApp template?`,
      confirmLabel: template.status === 1 ? 'Disable' : 'Enable',
      confirmClass: template.status === 1 ? 'btn-warning' : 'btn-success'
    }).then((confirmed) => {
      if (!confirmed) return;
      this.clearMessages();
      this.service.toggleWhatsappTemplateStatus(template.id).subscribe({
        next: () => {
          this.successMessage = `Template ${action}d successfully.`;
          this.changeTemplate = true;
          this.reloadTable();
        },
        error: () => { this.errorMessage = `Failed to ${action} template.`; }
      });
    });
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
