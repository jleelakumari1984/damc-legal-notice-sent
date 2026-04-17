import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { NoticeTemplateService } from '../../core/services/notice-template.service';
import { SmsApprovalFormComponent } from './sms-approval-form/sms-approval-form.component';
import { WhatsappApprovalFormComponent } from './whatsapp-approval-form/whatsapp-approval-form.component';
import { DatatableHelper } from '../../shared/datatable/datatable.helper';
import { SmsPendingDatatable } from '../../shared/datatable/sms-pending-datatable';
import { WhatsappPendingDatatable } from '../../shared/datatable/whatsapp-pending-datatable';

@Component({
  selector: 'app-template-approvals',
  templateUrl: './template-approvals.component.html'
})
export class TemplateApprovalsComponent implements AfterViewInit, OnInit, OnDestroy {
  @ViewChild('smsApprovalForm') smsApprovalForm!: SmsApprovalFormComponent;
  @ViewChild('whatsappApprovalForm') whatsappApprovalForm!: WhatsappApprovalFormComponent;

  activeTab: 'sms' | 'whatsapp' = 'sms';
  errorMessage = '';
  showApproval = false;
  private readonly SMS_TABLE_ID = '#smsPendingTable';
  private readonly WA_TABLE_ID = '#waPendingTable';
  private readonly dtHelper = new DatatableHelper();

  constructor(private readonly templateService: NoticeTemplateService) { }

  ngOnInit(): void {

  }
  ngAfterViewInit(): void {
    this.initSmsTable();
    this.initWhatsappTable();
  }

  ngOnDestroy(): void {
    this.dtHelper.destroy(this.SMS_TABLE_ID);
    this.dtHelper.destroy(this.WA_TABLE_ID);
  }

  private initSmsTable(): void {
    const dt = new SmsPendingDatatable({
      service: this.templateService,
      callbacks: {
        onApprove: (t) => { this.showApproval = true; this.smsApprovalForm.open(t, false, true); },
        onReject: (t) => { this.showApproval = true; this.smsApprovalForm.open(t, true, true); },
        onError: (msg) => { this.errorMessage = msg; }
      }
    });
    this.dtHelper.initTable(this.SMS_TABLE_ID, dt);
  }

  private initWhatsappTable(): void {
    const dt = new WhatsappPendingDatatable({
      service: this.templateService,
      callbacks: {
        onApprove: (t) => { this.showApproval = true; this.whatsappApprovalForm.open(t, false, true); },
        onReject: (t) => { this.showApproval = true; this.whatsappApprovalForm.open(t, true, true); },
        onError: (msg) => { this.showApproval = true; this.errorMessage = msg; }
      }
    });
    this.dtHelper.initTable(this.WA_TABLE_ID, dt);
  }

  reloadBoth(): void {
    this.showApproval = false;
    this.dtHelper.reload(this.SMS_TABLE_ID);
    this.dtHelper.reload(this.WA_TABLE_ID);
  }

  onApprovalCancelled(): void {
    this.showApproval = false;
  }
}

