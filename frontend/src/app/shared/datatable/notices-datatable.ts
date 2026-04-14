import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { SendNoticesService } from '../../core/services/send-notices.service';
import { NoticeService } from '../../core/services/notice.service';

declare const $: any;

export interface NoticesTableCallbacks {
  onSmsConfig: (notice: any) => void;
  onWhatsappConfig: (notice: any) => void;
  onExcelConfig: (notice: any) => void;
  onError: (message: string) => void;
}

function statusBadge(status: string): string {
  switch (status?.toUpperCase()) {
    case 'SUCCESS': return 'bg-success';
    case 'FAILED': return 'bg-danger';
    case 'PENDING': return 'bg-warning text-dark';
    case 'PROCESSING': return 'bg-info text-dark';
    case 'CANCELLED': return 'bg-secondary';
    default: return 'bg-secondary';
  }
}

export class NoticesDatatable extends DataTable {
  constructor(private callbacks: NoticesTableCallbacks, private readonly service: NoticeService) {
    super();
  }

  build(): object {
    const callbacks = this.callbacks;
    return {
      ...BASE_DT_OPTIONS,
      ajax: (dtData: object, callback: (data: object) => void) => {
        this.service.getNoticeTypes().subscribe({
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
      order: [[2, 'desc']],
      columns: [
        { data: 'id', title: '#' },
        {
          data: 'name', title: 'Notice Name', className: 'text-nowrap',
          render: (d: string, t: string) =>
            t === 'display' ? `<span class="fw-semibold">${esc(d)}</span>` : d
        },
        {
          data: 'createdAt', title: 'Created At', className: 'text-nowrap',
          render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
        },
        {
          data: null, title: 'Configs', orderable: false, searchable: false,
          className: 'text-end text-nowrap',
          render: (d: null, t: string, row: any) => {
            let html = `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-sms" title="Configure SMS Text"><i class="fas fa-sms"></i></button>`;
            html += `<button class="btn btn-outline-success btn-sm me-1 dt-btn-whatsapp" title="Configure WhatsApp Text"><i class="fab fa-whatsapp"></i></button>`;
            html += `<button class="btn btn-outline-secondary btn-sm me-1 dt-btn-excel" title="Configure Excel Headers"><i class="fas fa-file-excel"></i></button>`;
            return html;
          }
        }
      ],
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-sms').on('click', () => callbacks.onSmsConfig(data));
        $(row).find('.dt-btn-whatsapp').on('click', () => callbacks.onWhatsappConfig(data));
        $(row).find('.dt-btn-excel').on('click', () => callbacks.onExcelConfig(data));
      }
    };
  }
}
