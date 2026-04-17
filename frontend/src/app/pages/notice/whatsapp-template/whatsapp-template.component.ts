import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../../core/services/notice-template.service';
import { WhatsappTemplate, NoticeType, WhatsappPendingTemplateResponse } from '../../../core/models/notices.model';
import { WhatsappTemplateFormUserComponent } from './whatsapp-template-form-user/whatsapp-template-form-user.component';
import { WhatsappTemplateViewComponent } from './whatsapp-template-view/whatsapp-template-view.component';
import { StorageService } from '../../../core/services/storage.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { WhatsappTemplatesDatatable } from '../../../shared/datatable/whatsapp-templates-datatable';
import { ConfirmModalService } from '../../../shared/confirm-modal/confirm-modal.service';
import { WhatsappApprovalFormComponent } from '../../template-approvals/whatsapp-approval-form/whatsapp-approval-form.component';
import { BaseComponent } from '../../../shared/base/base.component';

@Component({
  selector: 'app-whatsapp-template',
  templateUrl: './whatsapp-template.component.html'
})
export class WhatsappTemplateComponent   extends BaseComponent  implements OnInit, OnChanges, OnDestroy {
  @ViewChild(WhatsappTemplateFormUserComponent) userForm?: WhatsappTemplateFormUserComponent;
  @ViewChild(WhatsappTemplateViewComponent) viewComp?: WhatsappTemplateViewComponent;
  @ViewChild(WhatsappApprovalFormComponent) whatsappApprovalForm!: WhatsappApprovalFormComponent;

  @Input() selectedNotice: NoticeType | null = null;
  @Input() isActiveStatus: boolean | null = null;
  @Output() onClose = new EventEmitter<boolean>();
  changeTemplate = false;
  editTemplate: WhatsappTemplate | null = null;
  showApproval = false;
  successMessage = '';
  errorMessage = '';

  private readonly tableId = '#whatsappTemplatesTable';

  constructor(
    private readonly service: NoticeTemplateService,
     private readonly dtHelper: DatatableHelper,
    private readonly confirmService: ConfirmModalService
  ) { super(); }

 

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
  openApprovalForm(t: WhatsappTemplate, isReject: boolean): void {
    const template: WhatsappPendingTemplateResponse = {
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
    this.whatsappApprovalForm.open(template, isReject, true);
  }

  reloadTable(): void {
    this.dtHelper.reload(this.tableId);
  }

  openAddForm(): void {
    this.editTemplate = null;
    this.clearMessages();

    this.userForm?.open();
  }

  openEditForm(template: WhatsappTemplate): void {
    this.editTemplate = template;
    this.clearMessages();
    this.userForm?.open();
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
  onSaved(savedData: WhatsappTemplate): void {
    this.successMessage = this.editTemplate ? 'Template updated.' : 'Template created.';
    this.editTemplate = savedData;
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
