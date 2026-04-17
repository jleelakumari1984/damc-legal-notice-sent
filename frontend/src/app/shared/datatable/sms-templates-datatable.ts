import { BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeTemplateService } from '../../core/services/notice-template.service';
import { StorageService } from '../../core/services/storage.service';
import { SmsTemplate, TemplateApprovedStatus } from '../../core/models/notices.model';

declare const $: any;

export interface SmsTemplatesDatatableOptions {
  getStatus?: () => boolean | null;
  noticeId: number;
  isSuperAdmin: boolean;
  service: NoticeTemplateService;
  storageService: StorageService;
  callbacks: {
    onEdit: (template: SmsTemplate) => void;
    onView: (template: SmsTemplate) => void;
    onToggle: (template: SmsTemplate) => void;
    onApprove: (template: SmsTemplate) => void;
    onReject: (template: SmsTemplate) => void;
    onError: (message: string) => void;
  }
}

export class SmsTemplatesDatatable extends DataTable {
  constructor(private readonly options: SmsTemplatesDatatableOptions) {
    super();
  }

  build(): object {
    const { noticeId, isSuperAdmin, service, storageService, callbacks, getStatus } = this.options;

    const adminColumns = [
      {
        data: null, title: '#', orderable: false, searchable: false,
        render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
      },
      {
        data: 'userTemplateContent', title: 'User Template Content',
        render: (d: string) => `<span class="text-break d-inline-block" style="max-width:350px">${esc(d)}</span>`
      }, { data: 'senderId', title: 'Sender ID', render: (d: string) => esc(d) },
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
        data: 'numberOfMessage', title: 'Number of Messages',
        render: (d: number, t: string) => (t === 'sort' || t === 'type') ? d : d
      }, {
        data: 'createdAt', title: 'Created At',
        render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
      },
      {
        data: null, title: 'Actions', orderable: false, searchable: false,
        className: 'text-end text-nowrap',
        render: (_: any, __: any, row: any) => {
          const approved = row.approveStatus === TemplateApprovedStatus.APPROVED;

          const toggleLabel = row.status ? '<i class="fa-solid fa-toggle-off fs-6"></i>' : '<i class="fa-solid fa-toggle-on fs-6"></i>';
          const toggleTitle = row.status ? 'Disable' : 'Enable';
          const toggleCls = row.status ? 'btn-danger' : 'btn-success';
          let actionBtn = '';
          if (approved) {
            actionBtn += `<button class="btn btn-secondary btn-sm me-1 dt-btn-view"><i class="fas fa-eye"></i></button>`;
            actionBtn += `<button class="btn ${toggleCls} btn-sm dt-btn-toggle" title="${toggleTitle}">${toggleLabel}</button>`;
          } else {
            if (row.ownTemplate) {
              actionBtn += `<button class="btn btn-primary btn-sm me-1 dt-btn-edit" title='Edit Template'><i class="fas fa-edit fs-6"></i></button>`;
            }
            actionBtn += `<button class="btn btn-success btn-sm me-1 dt-btn-approve" title='Approve Template'><i class="fas fa-check me-1 fs-6"></i></button>` +
              `<button class="btn btn-danger btn-sm dt-btn-reject me-1 " title='Reject Template'><i class="fas fa-times me-1 fs-6"></i></button>`
              ;
          }

          return actionBtn;
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
        data: 'numberOfMessages', title: 'Number of Messages',
        render: (d: number, t: string) => (t === 'sort' || t === 'type') ? d : d
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
          const toggleLabel = row.status ? '<i class="fa-solid fa-toggle-off fs-6"></i>' : '<i class="fa-solid fa-toggle-on fs-6"></i>';
          const toggleTitle = row.status ? 'Disable' : 'Enable';
          const toggleCls = row.status ? 'btn-danger' : 'btn-success';
          let actionBtn = "";
          if (approved) {
            actionBtn += `<button class="btn btn-secondary btn-sm me-1 dt-btn-view"><i class="fas fa-eye"></i></button>`;
            actionBtn += `<button class="btn ${toggleCls} btn-sm dt-btn-toggle" title="${toggleTitle}">${toggleLabel}</button>`;
          } else {
            actionBtn += `<button class="btn btn-primary btn-sm me-1 dt-btn-edit"><i class="fas fa-edit fs-6"></i></button>`;
          }
          return actionBtn;
        }
      }
    ];

    return {
      ...BASE_DT_OPTIONS,
      ajax: (_dtParams: any, callback: (data: object) => void) => {
        service.getSmsTemplates(noticeId, getStatus ? getStatus() : null).subscribe({
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
        $(row).find('.dt-btn-approve').on('click', () => callbacks.onApprove(data));
        $(row).find('.dt-btn-reject').on('click', () => callbacks.onReject(data));
      }
    };
  }
}
