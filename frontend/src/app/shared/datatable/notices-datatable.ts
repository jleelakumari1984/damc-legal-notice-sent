import { environment } from '../../../environments/environment';
import { BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { SendNoticesService } from '../../core/services/send-notices.service';
import { NoticeService } from '../../core/services/notice.service';
import { NoticeReportRequest } from '../../core/models/report.notice';

declare const $: any;

export interface NoticesTableOptions {
  service: NoticeService;
  callbacks: {
    onSmsConfig: (notice: any) => void;
    onWhatsappConfig: (notice: any) => void;
    onExcelConfig: (notice: any) => void;
    onError: (message: string) => void;
  };
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
  constructor(private readonly options: NoticesTableOptions) {
    super();
  }

  build(): object {
    const { callbacks, service } = this.options;
    return {
      ...BASE_DT_OPTIONS,
      ajax: (dtParams: any, callback: (data: object) => void) => {
        const start = Number(dtParams.start) || 0;
        const length = Number(dtParams.length) || 25;
        let sortField = 'createdAt';
        let sortDir = 'desc';
        if (dtParams.order && dtParams.order.length) {
          const order = dtParams.order[0];
          const colIndex = order.column;
          sortDir = order.dir || 'desc';
          if (dtParams.columns && dtParams.columns[colIndex]) {
            sortField = dtParams.columns[colIndex].data || sortField;
          }
        }
        var request: NoticeReportRequest = {
          sortColumn: sortField,
          sortDirection: sortDir,
          dtStart: start,
          dtLength: length,
          dtDraw: dtParams.draw,
        }
        service.getNoticeTypes(request).subscribe({
          next: (logs) => {
            callback({ draw: logs.draw, recordsTotal: logs.recordsTotal, recordsFiltered: logs.recordsFiltered, data: logs.data });
          },
          error: () => {
            var msg = 'Failed to load notice types';
            callbacks.onError(msg);
            callback({ draw: request.dtDraw, recordsTotal: 0, recordsFiltered: 0, data: [] });
          }
        });
      },
      order: [[5, 'desc']],
      columns: [
        { data: 'id', title: '#' },
        {
          data: 'name', title: 'Notice Name', className: 'text-nowrap',
          render: (d: string, t: string) =>
            t === 'display' ? `<span class="fw-semibold">${esc(d)}</span>` : d
        },

        {
          data: 'excelMapCount', title: 'Active Excel Fields', className: 'text-nowrap',
          render: (d: string, t: string) =>
            t === 'display' ? `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-excel" title="Configure Excel Headers"><span class="fw-semibold">${esc(d)}</span></button>` : d
        },
        {
          data: 'smsMapCount', title: 'Active SMS Templates', className: 'text-nowrap',
          render: (d: string, t: string) =>
            t === 'display' ? `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-sms" title="Configure SMS Text"><span class="fw-semibold">${esc(d)}</span></button>` : d
        },
        {
          data: 'whatsappMapCount', title: 'Active WhatsApp Templates', className: 'text-nowrap',
          render: (d: string, t: string) =>
            t === 'display' ? `<button class="btn btn-outline-success btn-sm me-1 dt-btn-whatsapp" title="Configure WhatsApp Text"><span class="fw-semibold">${esc(d)}</span></button>` : d
        },
        {
          data: 'createdAt', title: 'Created At', className: 'text-nowrap',
          render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
        }, /*  {
            data: 'mailMapCount', title: 'Mapped Mail Templates', className: 'text-nowrap',
            render: (d: string, t: string) =>
              t === 'display' ? `<span class="fw-semibold">${esc(d)}</span>` : d
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
        }*/
      ],
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-sms').on('click', () => callbacks.onSmsConfig(data));
        $(row).find('.dt-btn-whatsapp').on('click', () => callbacks.onWhatsappConfig(data));
        $(row).find('.dt-btn-excel').on('click', () => callbacks.onExcelConfig(data));
      }
    };
  }
}
