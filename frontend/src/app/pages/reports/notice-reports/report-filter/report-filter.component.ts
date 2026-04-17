import { AfterViewInit, Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { NoticeReportFilter } from '../../../../core/models/report.notice';
import { User } from '../../../../core/models/user.model';
import { UserService } from '../../../../core/services/user.service';
import { FormService } from '../../../../core/services/form.service';

declare const $: any;

@Component({
  selector: 'app-report-filter',
  templateUrl: './report-filter.component.html'
})
export class ReportFilterComponent implements AfterViewInit, OnDestroy {
  @Output() filterChange = new EventEmitter<NoticeReportFilter>();

  noticeName = '';
  status = '';
  userId: number | null = null;
  fromDate: Date | null = null;
  toDate: Date | null = null;

  users: User[] = [];
  readonly statuses = ['PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'CANCELLED'];

  constructor(
    private readonly userService: UserService,
    private readonly formService: FormService
  ) {}

  ngAfterViewInit(): void {
    this.userService.getAll().subscribe({
      next: (res) => { this.users = res.data; },
      error: () => { this.users = []; }
    });
  }

  open(): void {
    $('#reportFilterModal').modal('show');
    $('#reportFilterModal').one('shown.bs.modal', () => {
      this.formService.initSelect2(
        '#rf-status',
        (val) => { this.status = val; },
        { placeholder: 'All statuses' }
      );
      this.formService.initSelect2(
        '#rf-userId',
        (val) => { this.userId = val ? Number(val) : null; },
        { placeholder: 'All users' }
      );
      this.formService.initDatetimePicker(
        '#rf-fromDate',
        (date) => { this.fromDate = date; }
      );
      this.formService.initDatetimePicker(
        '#rf-toDate',
        (date) => { this.toDate = date; }
      );
    });
  }

  apply(): void {
    this.filterChange.emit(this.buildFilter());
    $('#reportFilterModal').modal('hide');
  }

  reset(): void {
    this.noticeName = '';
    this.status = '';
    this.userId = null;
    this.fromDate = null;
    this.toDate = null;

    $('#rf-status').val('').trigger('change.select2');
    $('#rf-userId').val('').trigger('change.select2');

    const fromEl = document.querySelector('#rf-fromDate') as any;
    const toEl = document.querySelector('#rf-toDate') as any;
    if (fromEl?._flatpickr) fromEl._flatpickr.clear();
    if (toEl?._flatpickr) toEl._flatpickr.clear();

    this.filterChange.emit({});
    $('#reportFilterModal').modal('hide');
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
    this.formService.destroySelect2('#rf-status');
    this.formService.destroySelect2('#rf-userId');
    this.formService.destroyDatetimePicker('#rf-fromDate');
    this.formService.destroyDatetimePicker('#rf-toDate');
  }
}
