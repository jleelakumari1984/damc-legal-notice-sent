import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';

declare const $: any;

export interface CreditsTableCallbacks {
  onEdit: (credit: any) => void;
  onDelete: (credit: any) => void;
  onDataLoaded: (data: any[]) => void;
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
  constructor(private callbacks: CreditsTableCallbacks) {
    super();
  }

  build(): object {
    const callbacks = this.callbacks;
    return {
    ...BASE_DT_OPTIONS,
    ajax: {
      url: `${environment.apiBaseUrl}/credits`,
      dataSrc: '',
      beforeSend: authBeforeSend()
    },
    order: [[8, 'desc']],
    columns: [
      { data: 'id', title: '#' },
      {
        data: 'userName', title: 'User',
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
      {
        data: null, title: 'Actions', orderable: false, searchable: false,
        className: 'text-end text-nowrap',
        render: () =>
          `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-edit">Edit</button>` +
          `<button class="btn btn-outline-danger btn-sm dt-btn-delete">Delete</button>`
      }
    ],
    createdRow: (row: HTMLElement, data: any) => {
      $(row).find('.dt-btn-edit').on('click', () => callbacks.onEdit(data));
      $(row).find('.dt-btn-delete').on('click', () => callbacks.onDelete(data));
    },
    initComplete: function (this: any) {
      const allData: any[] = this.api().data().toArray();
      callbacks.onDataLoaded(allData);
    }
  };
  }
}
