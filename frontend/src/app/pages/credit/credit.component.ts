import { AfterViewInit, Component, ViewChild } from '@angular/core';

import { DatatableHelper } from '../../shared/datatable/datatable.helper';
import { CreditsDatatable } from '../../shared/datatable/credits-datatable';
import {  CreditService } from '../../core/services/credit.service';
import { CreditFormComponent } from './credit-form/credit-form.component';
import { Credit } from '../../core/models/credit.model';

@Component({
  selector: 'app-credit',
  templateUrl: './credit.component.html',
  styleUrls: ['./credit.component.css']
})
export class CreditComponent implements AfterViewInit {
  @ViewChild(CreditFormComponent) creditForm!: CreditFormComponent;

  editCredit: Credit | null = null;
  deletingId: number | null = null;

  totalRecords = 0;
  smsBalance = 0;
  whatsappBalance = 0;
  emailBalance = 0;

  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly service: CreditService,
    private readonly datatableHelper: DatatableHelper
  ) {}

  ngAfterViewInit(): void {
    this.initTable();
  }

  private initTable(): void {
    this.datatableHelper.initTable('#creditsTable', new CreditsDatatable({
      onEdit: (credit) => this.openEditModal(credit),
      onDelete: (credit) => this.confirmDelete(credit),
      onDataLoaded: (data) => this.computeSummary(data)
    }));
  }

  private computeSummary(data: Credit[]): void {
    this.totalRecords = data.length;
    this.smsBalance = this.channelBalance(data, 'SMS');
    this.whatsappBalance = this.channelBalance(data, 'WHATSAPP');
    this.emailBalance = this.channelBalance(data, 'EMAIL');
  }

  private channelBalance(data: Credit[], channel: string): number {
    return data
      .filter(c => c.channel === channel)
      .reduce((sum, c) => sum + (c.type === 'CREDIT' ? c.credits : -c.credits), 0);
  }

  reload(): void {
    this.clearMessages();
    this.datatableHelper.reload('#creditsTable');
  }

  openAddModal(): void {
    this.editCredit = null;
    this.clearMessages();
    this.creditForm.open();
  }

  openEditModal(credit: Credit): void {
    this.editCredit = credit;
    this.clearMessages();
    this.creditForm.open();
  }

  onSaved(): void {
    this.successMessage = this.editCredit ? 'Credit record updated.' : 'Credit record created.';
    this.editCredit = null;
    this.datatableHelper.reload('#creditsTable');
  }

  confirmDelete(credit: Credit): void {
    if (!confirm(`Delete credit record #${credit.id}?`)) return;
    this.deletingId = credit.id;
    this.clearMessages();
    this.service.delete(credit.id).subscribe({
      next: () => {
        this.deletingId = null;
        this.successMessage = 'Credit record deleted.';
        this.datatableHelper.reload('#creditsTable');
      },
      error: () => {
        this.deletingId = null;
        this.errorMessage = 'Failed to delete record.';
      }
    });
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
