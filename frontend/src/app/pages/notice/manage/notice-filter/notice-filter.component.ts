import { AfterViewInit, Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { NoticeReportFilter } from '../../../../core/models/report.notice';
import { User } from '../../../../core/models/user.model';
import { UserService } from '../../../../core/services/user.service';
import { FormService } from '../../../../core/services/form.service';

declare const $: any;

@Component({
  selector: 'app-notice-filter',
  templateUrl: './notice-filter.component.html'
})
export class NoticeFilterComponent implements AfterViewInit, OnDestroy {
  @Output() filterChange = new EventEmitter<NoticeReportFilter>();

  noticeName = '';
  status = '';
  userId: number | null = null;
  fromDate: Date | null = null;
  toDate: Date | null = null;

  users: User[] = [];

  readonly statuses = ['PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'];

  constructor(
    private readonly userService: UserService,
    private readonly formService: FormService
  ) { }

  ngAfterViewInit(): void {
    this.userService.getAll().subscribe({
      next: (res) => { this.users = res.data; },
      error: () => { this.users = []; }
    });
    this.formService.initDatetimePicker(
      '#nf-fromDate',
      (date) => { this.fromDate = date; }
    );
    this.formService.initDatetimePicker(
      '#nf-toDate',
      (date) => { this.toDate = date; }
    );
    this.formService.initSelect2(
      '#nf-status',
      (val) => { this.status = val; },
      { placeholder: 'All statuses' }
    );
    this.formService.initSelect2(
      '#nf-userId',
      (val) => { this.userId = val ? Number(val) : null; },
      { placeholder: 'All users' }
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
    this.noticeName = '';
    this.status = '';
    this.userId = null;
    this.fromDate = null;
    this.toDate = null;

    // Reset select2 visually
    $('#nf-status').val('').trigger('change.select2');
    $('#nf-userId').val('').trigger('change.select2');

    // Reset flatpickr
    const fromEl = document.querySelector('#nf-fromDate') as any;
    const toEl = document.querySelector('#nf-toDate') as any;
    if (fromEl?._flatpickr) fromEl._flatpickr.clear();
    if (toEl?._flatpickr) toEl._flatpickr.clear();

    this.filterChange.emit({});
    $('#noticeFilterModal').modal('hide');
  }

  buildFilter(): NoticeReportFilter {
    const f: NoticeReportFilter = {};
    if (this.noticeName.trim()) f.noticeName = this.noticeName.trim();
    if (this.status) f.status = this.status;
    if (this.userId) f.userId = this.userId;
    if (this.fromDate) f.fromDate = this.fromDate;
    if (this.toDate) f.toDate = this.toDate;
    return f;
  }

  ngOnDestroy(): void {
    this.formService.destroyDatetimePicker('#nf-fromDate');
    this.formService.destroyDatetimePicker('#nf-toDate');
    this.formService.destroySelect2('#nf-status');
    this.formService.destroySelect2('#nf-userId');
  }
}
