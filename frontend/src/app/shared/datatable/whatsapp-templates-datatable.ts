import { BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeTemplateService } from '../../core/services/notice-template.service';

declare const $: any;

export interface WhatsappTemplatesDatatableOptions {
  processId: number;
  isSuperAdmin: boolean;
  service: NoticeTemplateService;
  onEdit: (template: any) => void;
  onDelete: (template: any) => void;
  onError: (message: string) => void;
}

export class WhatsappTemplatesDatatable extends DataTable {
  constructor(private readonly options: WhatsappTemplatesDatatableOptions) {
    super();
  }

  build(): object {
    const { processId, isSuperAdmin, service, onEdit, onDelete, onError } = this.options;

    const adminColumns = [
      {
        data: null, title: '#', orderable: false, searchable: false,
        render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
      },
      { data: 'templateName', title: 'Template Name', render: (d: string) => esc(d) },
      { data: 'templateLang', title: 'Language', render: (d: string) => esc(d) },
      { data: 'templatePath', title: 'Template Path', render: (d: string) => d ? esc(d) : '<span class="text-muted">—</span>' },
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
        service.getWhatsappTemplates(processId).subscribe({
          next: (data) => callback({ data }),
          error: () => {
            onError('Failed to load WhatsApp templates.');
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
