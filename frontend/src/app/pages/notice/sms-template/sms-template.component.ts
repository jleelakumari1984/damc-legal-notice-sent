import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../../core/services/notice-template.service';
import { SmsTemplate, NoticeType } from '../../../core/models/notices.model';
import { SmsTemplateFormComponent } from './sms-template-form/sms-template-form.component';
import { SmsTemplateFormUserComponent } from './sms-template-form-user/sms-template-form-user.component';
import { AuthService } from '../../../core/services/auth/auth.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { SmsTemplatesDatatable } from '../../../shared/datatable/sms-templates-datatable';

@Component({
  selector: 'app-sms-template',
  templateUrl: './sms-template.component.html'
})
export class SmsTemplateComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild(SmsTemplateFormComponent) adminForm?: SmsTemplateFormComponent;
  @ViewChild(SmsTemplateFormUserComponent) userForm?: SmsTemplateFormUserComponent;

  @Input() selectedNotice: NoticeType | null = null;
  @Output() onClose = new EventEmitter<void>();

  editTemplate: SmsTemplate | null = null;
  successMessage = '';
  errorMessage = '';

  private readonly tableId = '#smsTemplatesTable';

  constructor(
    private readonly service: NoticeTemplateService,
    private readonly authService: AuthService,
    private readonly dtHelper: DatatableHelper
  ) { }

  get isSuperAdmin(): boolean {
    return this.authService.isSuperAdmin();
  }

  ngOnInit(): void {
    if (this.selectedNotice) {
      this.initTable();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedNotice'] && this.selectedNotice) {
      this.initTable();
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
      onEdit: (t) => this.openEditForm(t),
      onDelete: (t) => this.confirmDelete(t),
      onError: (msg) => { this.errorMessage = msg; }
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

  confirmDelete(template: SmsTemplate): void {
    if (!confirm(`Delete SMS template "${template.templateId || template.senderId}"?`)) return;
    this.errorMessage = '';
    this.service.deleteSmsTemplate(template.id).subscribe({
      next: () => {
        this.successMessage = 'Template deleted.';
        this.reloadTable();
      },
      error: () => {
        this.errorMessage = 'Failed to delete template.';
      }
    });
  }

  onSaved(): void {
    this.successMessage = this.editTemplate ? 'Template updated.' : 'Template created.';
    this.editTemplate = null;
    this.reloadTable();
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
