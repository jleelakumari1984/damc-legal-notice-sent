import { BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeTemplateService } from '../../core/services/notice-template.service';

declare const $: any;

export interface SmsTemplatesDatatableOptions {
  processId: number;
  isSuperAdmin: boolean;
  service: NoticeTemplateService;
  onEdit: (template: any) => void;
  onDelete: (template: any) => void;
  onError: (message: string) => void;
}

export class SmsTemplatesDatatable extends DataTable {
  constructor(private readonly options: SmsTemplatesDatatableOptions) {
    super();
  }

  build(): object {
    const { processId, isSuperAdmin, service, onEdit, onDelete, onError } = this.options;

    const adminColumns = [
      {
        data: null, title: '#', orderable: false, searchable: false,
        render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
      },
      { data: 'senderId', title: 'Sender ID', render: (d: string) => esc(d) },
      { data: 'templateId', title: 'Template ID', render: (d: string) => esc(d) },
      { data: 'peid', title: 'PEID', render: (d: string) => d ? esc(d) : '<span class="text-muted">—</span>' },
      { data: 'routeId', title: 'Route ID', render: (d: string) => d ? esc(d) : '<span class="text-muted">—</span>' },
      { data: 'channel', title: 'Channel', render: (d: string) => d ? esc(d) : '<span class="text-muted">—</span>' },
      { data: 'dcs', title: 'DCS', render: (d: number) => String(d ?? 0) },
      {
        data: 'flashSms', title: 'Flash SMS',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${d === 1 ? 'bg-info text-dark' : 'bg-secondary'}">${d === 1 ? 'Yes' : 'No'}</span>`;
        }
      },
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

    const userColumns = [
      {
        data: null, title: '#', orderable: false, searchable: false,
        render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
      },
      {
        data: 'userTemplateContent', title: 'Template Content',
        render: (d: string) => `<span class="text-break d-inline-block" style="max-width:350px">${esc(d)}</span>`
      },
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

    return {
      ...BASE_DT_OPTIONS,
      ajax: (_dtParams: any, callback: (data: object) => void) => {
        service.getSmsTemplates(processId).subscribe({
          next: (data) => callback({ data }),
          error: () => {
            onError('Failed to load SMS templates.');
            callback({ data: [] });
          }
        });
      },
      columns: isSuperAdmin ? adminColumns : userColumns,
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-edit').on('click', () => onEdit(data));
        $(row).find('.dt-btn-delete').on('click', () => onDelete(data));
      }
    };
  }
}
