import { BASE_DT_OPTIONS, esc, formatDateTime } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeTemplateService } from '../../core/services/notice-template.service';
import { SmsPendingTemplate, SmsPendingTemplateRequest, SmsTemplate } from '../../core/models/notices.model';
import { PaginatedRequest } from '../../core/models/datatable.model';

declare const $: any;

export interface SmsPendingDatatableOptions {
    service: NoticeTemplateService;
    callbacks: {
        onApprove: (template: SmsPendingTemplate) => void;
        onReject: (template: SmsPendingTemplate) => void;
        onError: (message: string) => void;
    };
}

export class SmsPendingDatatable extends DataTable {
    constructor(private readonly options: SmsPendingDatatableOptions) {
        super();
    }

    build(): object {
        const { service, callbacks } = this.options;

        const columns = [
            {
                data: null, title: '#', orderable: false, searchable: false,
                render: (_: any, __: any, ___: any, meta: any) => meta.row + 1
            },
            { data: 'userName', title: 'Request user', render: (d: string) => String(d) },
            { data: 'noticeName', title: 'Notice', render: (d: string) => String(d) },

            {
                data: 'userTemplateContent', title: 'User Template',
                render: (d: string) => `<span class="text-break d-inline-block" style="max-width:300px">${esc(d)}</span>`
            },
            {
                data: 'createdAt', title: 'Submitted At',
                render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
            },
            {
                data: null, title: 'Actions', orderable: false, searchable: false,
                className: 'text-end text-nowrap',
                render: () =>
                    `<button class="btn btn-success btn-sm me-1 dt-btn-approve"><i class="fas fa-check me-1"></i>Approve</button>` +
                    `<button class="btn btn-danger btn-sm dt-btn-reject"><i class="fas fa-times me-1"></i>Reject</button>`
            }
        ];

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
                var request: PaginatedRequest<SmsPendingTemplateRequest> = {
                    sortColumn: sortField,
                    sortDirection: sortDir,
                    dtStart: start,
                    dtLength: length,
                    dtDraw: dtParams.draw,
                    filter: {}
                }
                service.getPendingSmsTemplates(request).subscribe({
                    next: (users) => {
                        callback({ draw: users.draw, recordsTotal: users.recordsTotal, recordsFiltered: users.recordsFiltered, data: users.data });
                    },
                    error: () => {
                        var msg = 'Failed to load pending SMS templates';
                        callbacks.onError(msg);
                        callback({ draw: request.dtDraw, recordsTotal: 0, recordsFiltered: 0, data: [] });
                    }
                });
            },
            columns,
            createdRow: (row: HTMLElement, data: SmsPendingTemplate) => {
                $(row).find('.dt-btn-approve').on('click', () => callbacks.onApprove(data));
                $(row).find('.dt-btn-reject').on('click', () => callbacks.onReject(data));
            }
        };
    }
}
