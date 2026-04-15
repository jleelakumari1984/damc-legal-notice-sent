import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../../core/services/notice-template.service';
import { SmsTemplate, NoticeType } from '../../../core/models/notices.model';
import { SmsTemplateFormComponent } from './sms-template-form/sms-template-form.component';
import { SmsTemplateFormUserComponent } from './sms-template-form-user/sms-template-form-user.component';
import { SmsTemplateViewComponent } from './sms-template-view/sms-template-view.component';
import { StorageService } from '../../../core/services/storage.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { SmsTemplatesDatatable } from '../../../shared/datatable/sms-templates-datatable';
import { ConfirmModalService } from '../../../shared/confirm-modal/confirm-modal.service';

@Component({
  selector: 'app-sms-template',
  templateUrl: './sms-template.component.html'
})
export class SmsTemplateComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild(SmsTemplateFormComponent) adminForm?: SmsTemplateFormComponent;
  @ViewChild(SmsTemplateFormUserComponent) userForm?: SmsTemplateFormUserComponent;
  @ViewChild(SmsTemplateViewComponent) viewComp?: SmsTemplateViewComponent;

  @Input() selectedNotice: NoticeType | null = null;
  @Output() onClose = new EventEmitter<boolean>();
  changeTemplate = false;
  editTemplate: SmsTemplate | null = null;
  successMessage = '';
  errorMessage = '';

  private readonly tableId = '#smsTemplatesTable';

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
      //this.initTable();
    }
  }

  ngOnDestroy(): void {
    this.dtHelper.destroy(this.tableId);
  }

  private initTable(): void {
    if (!this.selectedNotice) return;
    const dt = new SmsTemplatesDatatable({
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

  openEditForm(template: SmsTemplate): void {
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

  toggleStatus(template: SmsTemplate): void {
    const action = template.status === 1 ? 'disable' : 'enable';
    this.confirmService.confirm({
      title: `${template.status === 1 ? 'Disable' : 'Enable'} Template`,
      message: `Are you sure you want to ${action} this SMS template?`,
      confirmLabel: template.status === 1 ? 'Disable' : 'Enable',
      confirmClass: template.status === 1 ? 'btn-warning' : 'btn-success'
    }).then((confirmed) => {
      if (!confirmed) return;
      this.clearMessages();
      this.service.toggleSmsTemplateStatus(template.id).subscribe({
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
