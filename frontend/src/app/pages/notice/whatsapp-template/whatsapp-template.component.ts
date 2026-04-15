import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../../core/services/notice-template.service';
import { WhatsappTemplate, NoticeType } from '../../../core/models/notices.model';
import { WhatsappTemplateFormComponent } from './whatsapp-template-form/whatsapp-template-form.component';
import { WhatsappTemplateFormUserComponent } from './whatsapp-template-form-user/whatsapp-template-form-user.component';
import { AuthService } from '../../../core/services/auth/auth.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { WhatsappTemplatesDatatable } from '../../../shared/datatable/whatsapp-templates-datatable';

@Component({
  selector: 'app-whatsapp-template',
  templateUrl: './whatsapp-template.component.html'
})
export class WhatsappTemplateComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild(WhatsappTemplateFormComponent) adminForm?: WhatsappTemplateFormComponent;
  @ViewChild(WhatsappTemplateFormUserComponent) userForm?: WhatsappTemplateFormUserComponent;

  @Input() selectedNotice: NoticeType | null = null;
  @Output() onClose = new EventEmitter<void>();

  editTemplate: WhatsappTemplate | null = null;
  successMessage = '';
  errorMessage = '';

  private readonly tableId = '#whatsappTemplatesTable';

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
    const dt = new WhatsappTemplatesDatatable({
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

  openEditForm(template: WhatsappTemplate): void {
    this.editTemplate = template;
    this.clearMessages();
    if (this.isSuperAdmin) {
      this.adminForm?.open();
    } else {
      this.userForm?.open();
    }
  }

  confirmDelete(template: WhatsappTemplate): void {
    if (!confirm(`Delete WhatsApp template "${template.templateName || template.userTemplateContent}"?`)) return;
    this.errorMessage = '';
    this.service.deleteWhatsappTemplate(template.id).subscribe({
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
