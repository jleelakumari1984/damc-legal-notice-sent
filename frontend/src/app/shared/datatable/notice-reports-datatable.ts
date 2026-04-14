import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime, statusBadgeClass } from './datatable.utils';
import { DataTable } from './base-datatable';

declare const $: any;

export interface NoticeReportsTableCallbacks {
  onViewDetail: (id: number) => void;
  onError: (message: string) => void;
}

export class NoticeReportsDatatable extends DataTable {
  constructor(private callbacks: NoticeReportsTableCallbacks) {
    super();
  }

  build(): object {
    const callbacks = this.callbacks;
    return {
    ...BASE_DT_OPTIONS,
    ajax: (dtData: object, callback: (data: object) => void) => {
      $.ajax({
        url: `${environment.apiBaseUrl}/reports/notices`,
        beforeSend: authBeforeSend(),
        success: (data: unknown[]) => callback({ data }),
        error: (xhr: XMLHttpRequest) => {
          const status = xhr.status;
          const msg = status === 401 ? 'Unauthorized. Please log in again.'
                    : status === 403 ? 'Access denied.'
                    : status === 0   ? 'Network error. Please check your connection.'
                    : `Failed to load notices (HTTP ${status}).`;
          callbacks.onError(msg);
          callback({ data: [] });
        }
      });
    },
    order: [[6, 'desc']],
    columns: [
      { data: 'id', title: '#' },
      { data: 'originalFileName', title: 'File Name',className: 'text-nowrap', render: (d: string) => esc(d) },
      { data: 'processName', title: 'Process Name' ,className: 'text-nowrap'},
      {
        data: 'sendSms', title: 'SMS',className: 'text-nowrap',
        render: (d: boolean, t: string) => {
          if (t !== 'display') return d ? '1' : '0';
          const cls = d ? 'bg-success' : 'bg-secondary';
          return `<span class="badge ${cls}">${d ? 'Yes' : 'No'}</span>`;
        }
      },
      {
        data: 'sendWhatsapp', title: 'WhatsApp',className: 'text-nowrap',
        render: (d: boolean, t: string) => {
          if (t !== 'display') return d ? '1' : '0';
          const cls = d ? 'bg-success' : 'bg-secondary';
          return `<span class="badge ${cls}">${d ? 'Yes' : 'No'}</span>`;
        }
      },
      {
        data: 'status', title: 'Status',className: 'text-nowrap',
        render: (d: string, t: string) => {
          if (t !== 'display') return d;
          return `<span class="badge ${statusBadgeClass(d)}">${esc(d)}</span>`;
        }
      },
      {
        data: 'createdAt', title: 'Created At',
        render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
      },
      {
        data: null, title: '', orderable: false, searchable: false,
        render: () => `<button class="btn btn-brand btn-sm dt-btn-detail">View Details</button>`
      }
    ],
    createdRow: (row: HTMLElement, data: any) => {
      $(row).find('.dt-btn-detail').on('click', () => callbacks.onViewDetail(data.id));
    }
  };
  }
}
