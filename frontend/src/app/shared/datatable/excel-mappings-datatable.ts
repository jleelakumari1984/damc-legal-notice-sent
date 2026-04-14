import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';

declare const $: any;

export interface ExcelMappingsTableCallbacks {
  onEdit: (mapping: any) => void;
  onDelete: (mapping: any) => void;
}

function flagBadge(v: number): string {
  return v === 1 ? 'bg-success' : 'bg-secondary';
}

function flagLabel(v: number): string {
  return v === 1 ? 'Yes' : 'No';
}

export class ExcelMappingsDatatable extends DataTable {
  constructor(
    private processId: number,
    private callbacks: ExcelMappingsTableCallbacks
  ) {
    super();
  }

  build(): object {
    const processId = this.processId;
    const callbacks = this.callbacks;
    return {
    ...BASE_DT_OPTIONS,
    ajax: {
      url: `${environment.apiBaseUrl}/excel-mappings?processId=${processId}`,
      dataSrc: '',
      beforeSend: authBeforeSend()
    },
    columns: [
      { data: 'id', title: '#' },
      { data: 'excelFieldName', title: 'Excel Field Name', render: (d: string) => esc(d) },
      { data: 'dbFieldName', title: 'DB Field Name', render: (d: string) => esc(d) },
      {
        data: 'isKey', title: 'Key',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${flagBadge(d)}">${flagLabel(d)}</span>`;
        }
      },
      {
        data: 'isMobile', title: 'Mobile',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${flagBadge(d)}">${flagLabel(d)}</span>`;
        }
      },
      {
        data: 'isMandatory', title: 'Mandatory',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${flagBadge(d)}">${flagLabel(d)}</span>`;
        }
      },
      {
        data: 'isAttachment', title: 'Attachment',
        render: (d: number, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${flagBadge(d)}">${flagLabel(d)}</span>`;
        }
      },
      {
        data: 'createdAt', title: 'Created At',
        render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
      },
      {
        data: null, title: '', orderable: false, searchable: false,
        className: 'text-nowrap',
        render: () =>
          `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-edit">Edit</button>` +
          `<button class="btn btn-outline-danger btn-sm dt-btn-delete">Delete</button>`
      }
    ],
    createdRow: (row: HTMLElement, data: any) => {
      $(row).find('.dt-btn-edit').on('click', () => callbacks.onEdit(data));
      $(row).find('.dt-btn-delete').on('click', () => callbacks.onDelete(data));
    }
  };
  }
}
