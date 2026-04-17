import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeExcelMappingsService } from '../../core/services/notice-excel-mappings.service';
import { StorageService } from '../../core/services/storage.service';

declare const $: any;

export interface ExcelMappingsTableOptions {
  noticeId: number;
  excelMapService: NoticeExcelMappingsService;
  storageService: StorageService;
  callbacks: {
    onEdit: (mapping: any) => void;
    onDelete: (mapping: any) => void;
    onError: (message: string) => void;
  };
}

function flagBadge(v: number): string {
  return v === 1 ? 'bg-success' : 'bg-secondary';
}

function flagLabel(v: number): string {
  return v === 1 ? 'Yes' : 'No';
}

export class ExcelMappingsDatatable extends DataTable {
  constructor(private readonly options: ExcelMappingsTableOptions) {
    super();
  }

  build(): object {
    const { noticeId, callbacks, excelMapService, storageService } = this.options;
    return {
      ...BASE_DT_OPTIONS,

      ajax: (dtData: object, callback: (data: object) => void) => {
        excelMapService.getByNoticeId(noticeId).subscribe({
          next: (types) => {
            callback({ data: types }); // Load table first, then fetch notices
          },
          error: () => {
            var msg = 'Failed to load notice types';
            callbacks.onError(msg);
            callback({ data: [] });
          }
        });
      },
      ajax1: {
        url: `${environment.apiBaseUrl}/excel-mappings?noticeId=${noticeId}`,
        dataSrc: '',
        beforeSend: authBeforeSend(storageService)
      },
      columns: [
        { data: 'id', title: '#' },
        { data: 'excelFieldName', title: 'Excel Field Name', render: (d: string) => esc(d) },
        { data: 'dbFieldName', title: 'DB Field Name', render: (d: string) => esc(d) },
        {
          data: 'isAgreement', title: 'Agreement',
          render: (d: number, t: string) => {
            if (t !== 'display') return d;
            return `<span class="badge ${flagBadge(d)}">${flagLabel(d)}</span>`;
          }
        },
        {
          data: 'isCustomerName', title: 'Customer Name',
          render: (d: number, t: string) => {
            if (t !== 'display') return d;
            return `<span class="badge ${flagBadge(d)}">${flagLabel(d)}</span>`;
          }
        }, {
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
            `<button class="btn btn-primary btn-sm me-1 dt-btn-edit">Edit</button>` +
            `<button class="btn btn-danger btn-sm dt-btn-delete">Delete</button>`
        }
      ],
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-edit').on('click', () => callbacks.onEdit(data));
        $(row).find('.dt-btn-delete').on('click', () => callbacks.onDelete(data));
      }
    };
  }
}
