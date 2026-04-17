import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { NoticeReportFilter } from '../../../../core/models/report.notice';
import { User } from '../../../../core/models/user.model';
import { UserService } from '../../../../core/services/user.service';
import { FormService } from '../../../../core/services/form.service';
import { StorageService } from '../../../../core/services/storage.service';
import { BaseComponent } from '../../../../shared/base/base.component';

declare const $: any;

@Component({
  selector: 'app-notice-filter',
  templateUrl: './notice-filter.component.html'
})
export class NoticeFilterComponent extends BaseComponent implements AfterViewInit, OnDestroy {
  @Output() filterChange = new EventEmitter<NoticeReportFilter>();
  @Input() activeFilter: NoticeReportFilter = {};


  users: User[] = [];

  readonly statuses = ['PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'];

  constructor(
    private readonly userService: UserService,
    private readonly formService: FormService,

  ) {
    super();
  }

  ngAfterViewInit(): void {

    this.userService.getAll().subscribe({
      next: (res) => { this.users = res.data; },
      error: () => { this.users = []; }
    });

    this.formService.initDatetimePicker(
      '#nf-fromDate',
      (date) => { this.activeFilter.fromDate = date; }
    );
    this.formService.initDatetimePicker(
      '#nf-toDate',
      (date) => { this.activeFilter.toDate = date; }
    );
    this.formService.initSelect2(
      '#nf-status',
      (val) => { this.activeFilter.status = val; },
      { placeholder: 'All statuses' }
    );
    this.formService.initSelect2(
      '#nf-userId',
      (val) => { this.activeFilter.userId = val ? Number(val) : null; },
      { placeholder: 'All users', defaultValue: this.activeFilter.userId ? String(this.activeFilter.userId) : '' }
    );
  }

  open(): void {

    $('#noticeFilterModal').modal('show');

  }

  apply(): void {
    this.filterChange.emit(this.buildFilter());
    $('#noticeFilterModal').modal('hide');
  }

  reset(): void {

    // Reset select2 visually
    $('.select2-hidden-accessible').trigger('set.default');
    // Reset flatpickr
    const fromEl = document.querySelector('#nf-fromDate') as any;
    const toEl = document.querySelector('#nf-toDate') as any;
    if (fromEl?._flatpickr) fromEl._flatpickr.clear();
    if (toEl?._flatpickr) toEl._flatpickr.clear();

    this.activeFilter.noticeName = '';
    this.activeFilter.status = '';
    this.activeFilter.userId = this.getUserId() ?? null;
    this.activeFilter.fromDate = null;
    this.activeFilter.toDate = null;

    this.filterChange.emit(this.activeFilter);
    $('#noticeFilterModal').modal('hide');
  }

  buildFilter(): NoticeReportFilter {
    const f: NoticeReportFilter = {};
    if (this.activeFilter.noticeName?.trim()) f.noticeName = this.activeFilter.noticeName.trim();
    if (this.activeFilter.status) f.status = this.activeFilter.status;
    if (this.activeFilter.userId) f.userId = this.activeFilter.userId;
    if (this.activeFilter.fromDate) f.fromDate = this.activeFilter.fromDate;
    if (this.activeFilter.toDate) f.toDate = this.activeFilter.toDate;
    return f;
  }

  ngOnDestroy(): void {
    this.formService.destroyDatetimePicker('#nf-fromDate');
    this.formService.destroyDatetimePicker('#nf-toDate');
    this.formService.destroySelect2('#nf-status');
    this.formService.destroySelect2('#nf-userId');
  }
}
