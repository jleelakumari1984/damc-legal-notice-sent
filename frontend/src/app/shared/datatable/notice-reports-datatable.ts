import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime, statusBadgeClass } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeReportsService } from '../../core/services/notice-reports.service';
import { ProcessingStatus } from '../../core/models/schedule.model';
import { NoticeReportRequest, NoticeReportSummary } from '../../core/models/report.notice';

declare const $: any;

export interface NoticeReportsTableCallbacks {
  onViewDetail: (id: NoticeReportSummary, status: string, itemCount: number) => void;
  onSmsDetail: (id: NoticeReportSummary) => void;
  onWhatsappDetail: (id: NoticeReportSummary) => void;
  onError: (message: string) => void;
}

export class NoticeReportsDatatable extends DataTable {
  constructor(private callbacks: NoticeReportsTableCallbacks, private service: NoticeReportsService) {
    super();
  }

  build(): object {
    const callbacks = this.callbacks;
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
        this.service.getAll(request).subscribe({
          next: (reports) => {
            callback({ data: reports }); // Load table first, then fetch notices
          },
          error: () => {
            var msg = 'Failed to load notice reports';
            callbacks.onError(msg);
            callback({ data: [] });
          }
        });
      },
      order: [[6, 'desc']],
      columns: [
        { data: 'id', title: '#' },
        { data: 'originalFileName', title: 'File Name', className: 'text-nowrap', render: (d: string) => esc(d) },
        { data: 'processName', title: 'Process Name', className: 'text-nowrap' },
        {
          data: 'sendSms', title: 'SMS', className: 'text-nowrap',
          render: (d: boolean, t: string) => {
            if (t !== 'display') return d ? '1' : '0';
            const cls = d ? 'bg-success' : 'bg-secondary';
            return `<span class="badge ${cls}">${d ? 'Yes' : 'No'}</span>`;
          }
        },
        {
          data: 'sendWhatsapp', title: 'WhatsApp', className: 'text-nowrap',
          render: (d: boolean, t: string) => {
            if (t !== 'display') return d ? '1' : '0';
            const cls = d ? 'bg-success' : 'bg-secondary';
            return `<span class="badge ${cls}">${d ? 'Yes' : 'No'}</span>`;
          }
        },
        {
          data: 'status', title: 'Status', className: 'text-nowrap',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="badge ${statusBadgeClass(d)}">${esc(d)}</span>`;
          }
        },
        {
          data: 'totalItems', title: 'Total', className: 'text-nowrap',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="btn btn-primary btn-sm  dt-btn-total-detail">${esc(d)}</span>`;
          }
        },
        {
          data: 'pendingItems', title: 'Pending', className: 'text-nowrap',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="btn btn-warning btn-sm  dt-btn-pending-detail">${esc(d)}</span>`;
          }
        }, {
          data: 'processingItems', title: 'Processing', className: 'text-nowrap',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="btn btn-info btn-sm  dt-btn-processing-detail">${esc(d)}</span>`;
          }
        }, {
          data: 'completedItems', title: 'Completed', className: 'text-nowrap',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="btn btn-success btn-sm  dt-btn-completed-detail">${esc(d)}</span>`;
          }
        }, {
          data: 'failedItems', title: 'Failed', className: 'text-nowrap',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="btn btn-danger btn-sm  dt-btn-failed-detail">${esc(d)}</span>`;
          }
        }, {
          data: 'createdAt', title: 'Created At',
          render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
        },
        {
          data: null, title: 'Logs', orderable: false, searchable: false,
          className: 'text-end text-nowrap',
          render: (d: null, t: string, row: any) => {
            let html = `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-sms" title="SMS Logs"><i class="fas fa-sms"></i></button>`;
            html += `<button class="btn btn-outline-success btn-sm me-1 dt-btn-whatsapp" title="WhatsApp Logs"><i class="fab fa-whatsapp"></i></button>`;
            return html;
          }
        }

      ],
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-total-detail').off('click').on('click', () => callbacks.onViewDetail(data, '', data.totalItems));
        $(row).find('.dt-btn-pending-detail').off('click').on('click', () => callbacks.onViewDetail(data, ProcessingStatus.PENDING, data.pendingItems));

        $(row).find('.dt-btn-processing-detail').off('click').on('click', () => callbacks.onViewDetail(data, ProcessingStatus.PROCESSING, data.processingItems));
        $(row).find('.dt-btn-completed-detail').off('click').on('click', () => callbacks.onViewDetail(data, ProcessingStatus.COMPLETED, data.completedItems));
        $(row).find('.dt-btn-failed-detail').off('click').on('click', () => callbacks.onViewDetail(data, ProcessingStatus.FAILED, data.failedItems));
        $(row).find('.dt-btn-sms').off('click').on('click', () => callbacks.onSmsDetail(data));
        $(row).find('.dt-btn-whatsapp').off('click').on('click', () => callbacks.onWhatsappDetail(data));
      }
    };
  }
}
