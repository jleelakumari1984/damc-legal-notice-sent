import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../../core/services/notice-template.service';
import { SmsTemplate, NoticeType, SmsPendingTemplateResponse } from '../../../core/models/notices.model';
import { SmsTemplateFormUserComponent } from './sms-template-form-user/sms-template-form-user.component';
import { SmsTemplateViewComponent } from './sms-template-view/sms-template-view.component';
import { StorageService } from '../../../core/services/storage.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { SmsTemplatesDatatable } from '../../../shared/datatable/sms-templates-datatable';
import { ConfirmModalService } from '../../../shared/confirm-modal/confirm-modal.service';
import { SmsApprovalFormComponent } from '../../template-approvals/sms-approval-form/sms-approval-form.component';
import { BaseComponent } from '../../../shared/base/base.component';

@Component({
  selector: 'app-sms-template',
  templateUrl: './sms-template.component.html'
})
export class SmsTemplateComponent extends BaseComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild(SmsTemplateFormUserComponent) userForm?: SmsTemplateFormUserComponent;
  @ViewChild(SmsTemplateViewComponent) viewComp?: SmsTemplateViewComponent;
  @ViewChild(SmsApprovalFormComponent) smsApprovalForm!: SmsApprovalFormComponent;

  @Input() selectedNotice: NoticeType | null = null;
  @Input() isActiveStatus: boolean | null = null;
  @Output() onClose = new EventEmitter<boolean>();

  changeTemplate = false;
  editTemplate: SmsTemplate | null = null;
  showApproval = false;
  successMessage = '';
  errorMessage = '';

  private readonly tableId = '#smsTemplatesTable';

  constructor(
    private readonly service: NoticeTemplateService,
    private readonly dtHelper: DatatableHelper,
    private readonly confirmService: ConfirmModalService
  ) {
    super();
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
      getStatus: () => this.isActiveStatus,
      noticeId: this.selectedNotice.id,
      service: this.service,
      storageService: this.storageService,
      callbacks: {
        onEdit: (t) => this.openEditForm(t),
        onView: (t) => this.viewComp?.open(t),
        onToggle: (t) => this.toggleStatus(t),
        onApprove: (t) => this.openApprovalForm(t, false),
        onReject: (t) => this.openApprovalForm(t, true),
        onError: (msg) => { this.errorMessage = msg; }
      }

    });
    setTimeout(() => this.dtHelper.initTable(this.tableId, dt));
  }
  openApprovalForm(t: SmsTemplate, isReject: boolean): void {
    const template: SmsPendingTemplateResponse = {
      id: t.id,
      noticeId: t.noticeId,
      noticeName: this.selectedNotice?.name ?? '',
      userName: '',
      userTemplateContent: t.userTemplateContent,
      templateContent: t.templateContent,
      messageLength: t.messageLength,
      numberOfMessage: t.numberOfMessage,
      createdAt: t.createdAt
    };
    this.showApproval = true;
    this.smsApprovalForm.open(template, isReject, true);
  }
  reloadTable(): void {
    this.dtHelper.reload(this.tableId);
  }

  openAddForm(): void {
    this.editTemplate = null;
    this.clearMessages();
    this.userForm?.open();
  }

  openEditForm(template: SmsTemplate): void {
    this.editTemplate = template;
    this.clearMessages();
    this.userForm?.open();
  }
  statusChanged(): void {

  }
  onApproved(): void {
    this.showApproval = false;
    this.successMessage = 'Template approval updated.';
    this.editTemplate = null;
    this.changeTemplate = true;
    this.reloadTable();
  }

  onApprovalCancelled(): void {
    this.showApproval = false;
  }
  onSaved(savedData: SmsTemplate): void {
    this.successMessage = this.editTemplate ? 'Template updated.' : 'Template created.';
    this.editTemplate = savedData;
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
