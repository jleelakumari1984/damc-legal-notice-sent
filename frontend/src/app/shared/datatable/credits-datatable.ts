import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { StorageService } from '../../core/services/storage.service';
import { CreditService } from '../../core/services/credit.service';
import { CreditPaginatedRequest } from '../../core/models/credit.model';

declare const $: any;

export interface CreditsTableOptions {
  storageService: StorageService;
  creditService: CreditService;
  userId?: number;
  callbacks: {
    onDataLoaded: (data: any[]) => void;
    onError: (message: string) => void;
  };
}

function channelBadge(channel: string): string {
  const map: Record<string, string> = {
    SMS: 'bg-info text-dark',
    WHATSAPP: 'bg-success',
    EMAIL: 'bg-primary'
  };
  return map[channel] ?? 'bg-secondary';
}

export class CreditsDatatable extends DataTable {
  constructor(private readonly options: CreditsTableOptions) {
    super();
  }

  build(): object {
    const { callbacks, storageService, userId, creditService } = this.options;
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
        var request: CreditPaginatedRequest = {
          sortColumn: sortField,
          sortDirection: sortDir,
          dtStart: start,
          dtLength: length,
          dtDraw: dtParams.draw,
          userid: userId
        }
        creditService.getAll(request).subscribe({
          next: (users) => {
            callback({ draw: users.draw, recordsTotal: users.recordsTotal, recordsFiltered: users.recordsFiltered, data: users.data });
          },
          error: () => {
            var msg = 'Failed to load users';
            callbacks.onError(msg);
            callback({ draw: request.dtDraw, recordsTotal: 0, recordsFiltered: 0, data: [] });
          }
        });


      },
      order: [[8, 'desc']],
      columns: [
        { data: 'id', title: '#' },
        {
          data: 'userName', title: 'User',
          visible: !userId,
          render: (d: string, t: string) =>
            t === 'display' ? `<span class="fw-semibold">${esc(d)}</span>` : d
        },
        {
          data: 'channel', title: 'Channel',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            return `<span class="badge ${channelBadge(d)}">${esc(d)}</span>`;
          }
        },
        {
          data: 'type', title: 'Type',
          render: (d: string, t: string) => {
            if (t !== 'display') return d;
            const cls = d === 'CREDIT' ? 'bg-success' : 'bg-danger';
            return `<span class="badge ${cls}">${esc(d)}</span>`;
          }
        },
        {
          data: 'credits', title: 'Units',
          render: (d: number, t: string, row: any) => {
            if (t !== 'display') return d;
            const colorCls = row.type === 'CREDIT' ? 'text-success' : 'text-danger';
            const sign = row.type === 'DEBIT' ? '-' : '+';
            return `<span class="fw-semibold ${colorCls}">${sign}${d.toLocaleString()}</span>`;
          }
        },
        {
          data: 'pricePerUnit', title: 'Price/Unit',
          render: (d: number, t: string) =>
            t === 'display' ? `&#8377;${d.toFixed(2)}` : d
        },
        { data: 'description', title: 'Description', render: (d: string) => esc(d) },
        {
          data: 'status', title: 'Status',
          render: (d: string, t: string) =>
            t === 'display' ? `<span class="badge bg-secondary">${esc(d)}</span>` : d
        },
        {
          data: 'createdAt', title: 'Created At',
          render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
        },

      ],
      createdRow: (row: HTMLElement, data: any) => {

      },
      drawCallback: function (this: any) {
        const allData: any[] = this.api().data().toArray();
        callbacks.onDataLoaded(allData);
      }
    };
  }
}
