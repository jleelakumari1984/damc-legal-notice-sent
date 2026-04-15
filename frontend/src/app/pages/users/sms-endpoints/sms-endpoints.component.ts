import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { SmsEndpointService } from '../../../core/services/sms-endpoint.service';
import { DatatableHelper } from '../../../shared/datatable/datatable.helper';
import { BASE_DT_OPTIONS, esc, formatDateTime } from '../../../shared/datatable/datatable.utils';
import { SmsEndpoint } from '../../../core/models/endpoint.model';

declare const $: any;

@Component({
  selector: 'app-sms-endpoints',
  templateUrl: './sms-endpoints.component.html'
})
export class SmsEndpointsComponent implements OnInit, OnDestroy {
  private readonly TABLE_ID = 'smsEndpointsTable';
  private readonly dtHelper = new DatatableHelper();

  editEndpoint: SmsEndpoint | null = null;
  form!: FormGroup;
  saving = false;
  deleting = false;
  errorMessage = '';
  formError = '';

  constructor(private readonly fb: FormBuilder, private readonly service: SmsEndpointService) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      host: ['', Validators.required],
      port: [80, [Validators.required, Validators.min(1), Validators.max(65535)]],
      username: ['', Validators.required],
      password: [''],
      apiPath: ['', Validators.required],
      status: [1]
    });

    setTimeout(() => this.initTable());
  }

  ngOnDestroy(): void {
    this.dtHelper.destroy(this.TABLE_ID);
  }

  private initTable(): void {
    const columns = [
      {
        data: null, title: '#', orderable: false, searchable: false,
        render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
      },
      { data: 'name', title: 'Name', render: (d: string) => esc(d) },
      { data: 'host', title: 'Host', render: (d: string) => esc(d) },
      { data: 'port', title: 'Port', render: (d: number) => String(d) },
      { data: 'username', title: 'Username', render: (d: string) => esc(d) },
      { data: 'apiPath', title: 'API Path', render: (d: string) => esc(d) },
      {
        data: 'status', title: 'Status',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${d === 1 ? 'bg-success' : 'bg-secondary'}">${d === 1 ? 'Active' : 'Inactive'}</span>`;
        }
      },
      {
        data: 'createdAt', title: 'Created At',
        render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
      },
      {
        data: null, title: 'Actions', orderable: false, searchable: false,
        className: 'text-end text-nowrap',
        render: () =>
          `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-edit"><i class="fas fa-edit"></i></button>` +
          `<button class="btn btn-outline-danger btn-sm dt-btn-delete"><i class="fas fa-trash"></i></button>`
      }
    ];

    this.dtHelper.initWithAjax(this.TABLE_ID, {
      ...BASE_DT_OPTIONS,
      ajax: (_dtParams: any, callback: (data: object) => void) => {
        this.service.getAll().subscribe({
          next: (data) => callback({ data }),
          error: () => {
            this.errorMessage = 'Failed to load SMS endpoints.';
            callback({ data: [] });
          }
        });
      },
      columns,
      createdRow: (row: HTMLElement, data: SmsEndpoint) => {
        $(row).find('.dt-btn-edit').on('click', () => this.openForm(data));
        $(row).find('.dt-btn-delete').on('click', () => this.delete(data));
      }
    });
  }

  openForm(endpoint?: SmsEndpoint): void {
    this.editEndpoint = endpoint ?? null;
    this.formError = '';
    if (endpoint) {
      this.form.patchValue({
        name: endpoint.name,
        host: endpoint.host,
        port: endpoint.port,
        username: endpoint.username,
        password: '',
        apiPath: endpoint.apiPath,
        status: endpoint.status
      });
    } else {
      this.form.reset({ port: 80, status: 1 });
    }
    $('#smsEndpointModal').modal('show');
  }

  cancel(): void {
    $('#smsEndpointModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    const request: any = {
      name: v.name.trim(),
      host: v.host.trim(),
      port: Number(v.port),
      username: v.username.trim(),
      apiPath: v.apiPath.trim(),
      status: v.status ? 1 : 0
    };
    if (v.password?.trim()) {
      request.password = v.password.trim();
    }
    this.saving = true;
    this.formError = '';
    const call$ = this.editEndpoint
      ? this.service.update(this.editEndpoint.id, request)
      : this.service.create(request);

    call$.subscribe({
      next: () => {
        this.saving = false;
        $('#smsEndpointModal').modal('hide');
        this.dtHelper.reload(this.TABLE_ID);
      },
      error: () => {
        this.saving = false;
        this.formError = 'Failed to save SMS endpoint.';
      }
    });
  }

  delete(endpoint: SmsEndpoint): void {
    if (!confirm(`Delete SMS endpoint "${endpoint.name}"?`)) return;
    this.service.delete(endpoint.id).subscribe({
      next: () => this.dtHelper.reload(this.TABLE_ID),
      error: () => { this.errorMessage = 'Failed to delete endpoint.'; }
    });
  }
}
