import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDate } from './datatable.utils';
import { DataTable } from './base-datatable';

declare const $: any;

export interface UsersTableCallbacks {
  onEdit: (user: any) => void;
  onDelete: (user: any) => void;
}

export class UsersDatatable extends DataTable {
  constructor(private callbacks: UsersTableCallbacks) {
    super();
  }

  build(): object {
    const callbacks = this.callbacks;
    return {
    ...BASE_DT_OPTIONS,
    ajax: {
      url: `${environment.apiBaseUrl}/users`,
      dataSrc: '',
      beforeSend: authBeforeSend()
    },
    columns: [
      { data: 'id', title: '#' },
      {
        data: 'name', title: 'Name',
        render: (d: string, t: string) =>
          t === 'display' ? `<span class="fw-semibold">${esc(d)}</span>` : d
      },
      { data: 'email', title: 'Email', render: (d: string) => esc(d) },
      { data: 'mobile', title: 'Mobile', render: (d: string) => esc(d) },
      {
        data: 'role', title: 'Role',
        render: (d: string, t: string) => {
          if (t !== 'display') return d;
          const cls = d === 'ADMIN' ? 'bg-danger' : d === 'OPERATOR' ? 'bg-primary' : 'bg-secondary';
          return `<span class="badge ${cls}">${esc(d)}</span>`;
        }
      },
      {
        data: 'status', title: 'Status',
        render: (d: string, t: string) => {
          if (t !== 'display') return d;
          const cls = d === 'ACTIVE' ? 'bg-success' : 'bg-secondary';
          return `<span class="badge ${cls}">${esc(d)}</span>`;
        }
      },
      {
        data: 'createdAt', title: 'Created At',
        render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDate(d)
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
    }
  };
  }
}
