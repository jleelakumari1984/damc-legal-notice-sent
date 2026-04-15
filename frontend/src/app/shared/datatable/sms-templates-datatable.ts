import { BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeTemplateService } from '../../core/services/notice-template.service';
import { StorageService } from '../../core/services/storage.service';
import { TemplateApprovedStatus } from '../../core/models/notices.model';

declare const $: any;

export interface SmsTemplatesDatatableOptions {
  processId: number;
  isSuperAdmin: boolean;
  service: NoticeTemplateService;
  storageService: StorageService;
  callbacks: {
    onEdit: (template: any) => void;
    onView: (template: any) => void;
    onToggle: (template: any) => void;
    onError: (message: string) => void;
  }
}

export class SmsTemplatesDatatable extends DataTable {
  constructor(private readonly options: SmsTemplatesDatatableOptions) {
    super();
  }

  build(): object {
    const { processId, isSuperAdmin, service, storageService, callbacks } = this.options;

    const adminColumns = [
      {
        data: null, title: '#', orderable: false, searchable: false,
        render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
      },
      { data: 'senderId', title: 'Sender ID', render: (d: string) => esc(d) },
      { data: 'templateId', title: 'Template ID', render: (d: string) => esc(d) },
      {
        data: 'status', title: 'Status',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${d === 1 ? 'bg-success' : 'bg-secondary'}">${d === 1 ? 'Active' : 'Inactive'}</span>`;
        }
      },
      {
        data: 'approveStatus', title: 'Approval',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          const map: Record<number, [string, string]> = {
            [TemplateApprovedStatus.PENDING]: ['Pending', 'bg-secondary'],
            [TemplateApprovedStatus.APPROVED]: ['Approved', 'bg-success text-dark'],
            [TemplateApprovedStatus.REJECT]: ['Reject', 'bg-danger']
          };
          return `<span class="badge ${map[d]?.[1] ?? 'bg-secondary'}">${map[d]?.[0] ?? 'Unknown'}</span>`;
        }
      },
      {
        data: 'createdAt', title: 'Created At',
        render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
      },
      {
        data: null, title: 'Actions', orderable: false, searchable: false,
        className: 'text-end text-nowrap',
        render: (_: any, __: any, row: any) => {
          const approved = row.approveStatus === TemplateApprovedStatus.APPROVED;

          const toggleLabel = row.status === 1 ? 'Disable' : 'Enable';
          const toggleCls = row.status === 1 ? 'btn-outline-warning' : 'btn-outline-success';
          const actionBtn = approved
            ? `<button class="btn btn-outline-secondary btn-sm me-1 dt-btn-view"><i class="fas fa-eye"></i></button>`
            : `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-edit"><i class="fas fa-edit"></i></button>`;
          return actionBtn +
            `<button class="btn ${toggleCls} btn-sm dt-btn-toggle">${toggleLabel}</button>`;
        }
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
        data: 'approveStatus', title: 'Approval',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          const map: Record<number, [string, string]> = {
            [TemplateApprovedStatus.PENDING]: ['Pending', 'bg-secondary'],
            [TemplateApprovedStatus.APPROVED]: ['Approved', 'bg-success text-dark'],
            [TemplateApprovedStatus.REJECT]: ['Reject', 'bg-danger']
          };
          return `<span class="badge ${map[d]?.[1] ?? 'bg-secondary'}">${map[d]?.[0] ?? 'Unknown'}</span>`;
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
        render: (_: any, __: any, row: any) => {
          const approved = row.approveStatus === TemplateApprovedStatus.APPROVED;
          const toggleLabel = row.status === 1 ? 'Disable' : 'Enable';
          const toggleCls = row.status === 1 ? 'btn-outline-warning' : 'btn-outline-success';
          const actionBtn = approved
            ? `<button class="btn btn-outline-secondary btn-sm me-1 dt-btn-view"><i class="fas fa-eye"></i></button>`
            : `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-edit"><i class="fas fa-edit"></i></button>`;
          return actionBtn +
            `<button class="btn ${toggleCls} btn-sm dt-btn-toggle">${toggleLabel}</button>`;
        }
      }
    ];

    return {
      ...BASE_DT_OPTIONS,
      ajax: (_dtParams: any, callback: (data: object) => void) => {
        service.getSmsTemplates(processId).subscribe({
          next: (data) => callback({ data }),
          error: () => {
            callbacks.onError('Failed to load SMS templates.');
            callback({ data: [] });
          }
        });
      },
      columns: isSuperAdmin ? adminColumns : userColumns,
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-edit').on('click', () => callbacks.onEdit(data));
        $(row).find('.dt-btn-view').on('click', () => callbacks.onView(data));
        $(row).find('.dt-btn-toggle').on('click', () => callbacks.onToggle(data));
      }
    };
  }
}
