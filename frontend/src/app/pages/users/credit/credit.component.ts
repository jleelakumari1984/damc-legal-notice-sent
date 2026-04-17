import { AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';

import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { CreditsDatatable } from '../../../shared/datatable/credits-datatable';
import { CreditService } from '../../../core/services/credit.service';
import { CreditFormComponent } from './credit-form/credit-form.component';
import { Credit } from '../../../core/models/credit.model';
import { StorageService } from '../../../core/services/storage.service';
import { BaseComponent } from '../../../shared/base/base.component';

@Component({
  selector: 'app-credit',
  templateUrl: './credit.component.html',
  styleUrls: ['./credit.component.css']
})
export class CreditComponent extends BaseComponent implements AfterViewInit, OnChanges {
  @Input() userId: number | null = null;
  @Input() userName: string = '';
  @Output() close = new EventEmitter<void>();

  @ViewChild(CreditFormComponent) creditForm!: CreditFormComponent;


  totalRecords = 0;
  smsBalance = 0;
  whatsappBalance = 0;
  emailBalance = 0;

  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly service: CreditService,
    private readonly datatableHelper: DatatableHelper,
  ) { super(); }

  ngAfterViewInit(): void {
    this.initTable();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userId'] && !changes['userId'].firstChange) {
      this.initTable();
    }
  }

  private initTable(): void {
    this.datatableHelper.initTable('#creditsTable', new CreditsDatatable({
      storageService: this.storageService,
      creditService: this.service,
      userId: this.userId ?? undefined,
      callbacks: {
        onDataLoaded: (data) => this.computeSummary(data),
        onError: (msg) => this.errorMessage = msg
      }
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
    this.clearMessages();
    this.creditForm.open();
  }
  onSaved(): void {
    this.successMessage = 'Credit record created.';
    this.datatableHelper.reload('#creditsTable');
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
