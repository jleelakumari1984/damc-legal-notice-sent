import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDate } from './datatable.utils';
import { DataTable } from './base-datatable';
import { StorageService } from '../../core/services/storage.service';
import { UserService } from '../../core/services/user.service';
import { UserPaginatedRequest } from '../../core/models/user.model';

declare const $: any;

export interface UsersTableOptions {
  storageService: StorageService;
  userService: UserService;
  callbacks: {
    onEdit: (user: any) => void;
    onToggle: (user: any) => void;
    onCredits: (user: any) => void;
    onPassword: (user: any) => void;
    onError: (message: string) => void;
  };
}

export class UsersDatatable extends DataTable {
  constructor(private readonly options: UsersTableOptions) {
    super();
  }

  build(): object {
    const { callbacks, storageService, userService } = this.options;
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
        var request: UserPaginatedRequest = {
          sortColumn: sortField,
          sortDirection: sortDir,
          dtStart: start,
          dtLength: length,
          dtDraw: dtParams.draw,
        }
        userService.getAll(request).subscribe({
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
      columns: [
        { data: 'id', title: '#' },
        {
          data: 'displayName', title: 'Name',
          render: (d: string, t: string) =>
            t === 'display' ? `<span class="fw-semibold">${esc(d)}</span>` : d
        },
        { data: 'userEmail', title: 'Email', render: (d: string) => esc(d) },
        { data: 'userMobileSms', title: 'Mobile', render: (d: string) => esc(d) },
        {
          data: 'accessLevel', title: 'Access Level',
          render: (d: number, t: string) => {
            if (t !== 'display') return d;
            const map: Record<number, [string, string]> = {
              1: ['Super Admin', 'bg-danger'],
              2: ['Admin', 'bg-warning text-dark'],
              3: ['User', 'bg-secondary']
            };
            const [label, cls] = map[d] ?? [`Level ${d}`, 'bg-secondary'];
            return `<span class="badge ${cls}">${label}</span>`;
          }
        },
        {
          data: 'enabled', title: 'Status',
          render: (d: boolean, t: string) => {
            if (t !== 'display') return String(d);
            return d
              ? `<span class="badge bg-success">Active</span>`
              : `<span class="badge bg-secondary">Inactive</span>`;
          }
        },
        {
          data: 'smsCredits', title: 'SMS Credits',
          render: (d: string) => esc(d)
        },
        {
          data: 'whatsappCredits', title: 'WhatsApp Credits',
          render: (d: string) => esc(d)
        },

        {
          data: 'createdAt', title: 'Created At',
          render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDate(d)
        },
        {
          data: null, title: 'Actions', orderable: false, searchable: false,
          defaultContent: '',
          className: 'text-end text-nowrap',
          render: (d: any, t: any, row: any) => {
            const toggleLabel = row.enabled ? 'Disable' : 'Enable';
            const toggleCls = row.enabled ? 'btn-outline-warning' : 'btn-outline-success';
            return `<button class="btn btn-outline-primary btn-sm me-1 dt-btn-edit">Edit</button>` +
              `<button class="btn ${toggleCls} btn-sm me-1 dt-btn-toggle">${toggleLabel}</button>` +
              `<button class="btn btn-outline-secondary btn-sm me-1 dt-btn-password">Password</button>` +
              `<button class="btn btn-outline-info btn-sm dt-btn-credits">Credits</button>`;
          }
        }
      ],
      createdRow: (row: HTMLElement, data: any) => {
        $(row).find('.dt-btn-edit').on('click', () => callbacks.onEdit(data));
        $(row).find('.dt-btn-toggle').on('click', () => callbacks.onToggle(data));
        $(row).find('.dt-btn-password').on('click', () => callbacks.onPassword(data));
        $(row).find('.dt-btn-credits').on('click', () => callbacks.onCredits(data));
      }
    };
  }
}
