import { environment } from '../../../environments/environment';
import { authBeforeSend, BASE_DT_OPTIONS, esc, formatDateTime, statusBadgeClass } from './datatable.utils';
import { DataTable } from './base-datatable';
import { NoticeReportsService } from '../../core/services/notice-reports.service';
import { WhatsappLogRequest } from '../../core/models/whatsapp.model';

declare const $: any;

export interface WhatsappLogsDatatableOptions {
    reportId: number;
    status: string;
    service: NoticeReportsService;
    onDownload: () => void;
    onError: (message: string) => void;
}

export class WhatsappLogsDatatable extends DataTable {
    constructor(private readonly options: WhatsappLogsDatatableOptions) {
        super();
    }

    build(): object {
        const { reportId, status, service, onError, onDownload } = this.options;
        const isErrorTab = status === 'error';
        $.fn.dataTable.feature.register('smsDownload', () => {
            const btn = document.createElement('button');
            btn.className = 'btn btn-success btn-sm ms-2';
            btn.innerHTML = '<i class="fas fa-file-csv me-1"></i> Download CSV';
            btn.addEventListener('click', () => onDownload());
            return btn;
        });
        return {
            ...BASE_DT_OPTIONS,
            serverSide: true,
            layout: {
                topStart: { pageLength: {}, smsDownload: {} },
            },
            ajax: (dtParams: any, callback: (data: object) => void) => {
                const start = Number(dtParams.start) || 0;
                const length = Number(dtParams.length) || 25;
                const page = Math.floor(start / length);

                let sortField = 'sendAt';
                let sortDir = 'desc';
                if (dtParams.order && dtParams.order.length) {
                    const order = dtParams.order[0];
                    const colIndex = order.column;
                    sortDir = order.dir || 'desc';
                    if (dtParams.columns && dtParams.columns[colIndex]) {
                        sortField = dtParams.columns[colIndex].data || sortField;
                    }
                }
                var request: WhatsappLogRequest = {
                    sortColumn: sortField,
                    sortDirection: sortDir,
                    dtStart: start,
                    dtLength: length,
                    dtDraw: dtParams.draw,
                    status: status
                }
                service.getWhatsappLogs(reportId, request).subscribe({
                    next: (logs) => {
                        callback({ draw: logs.draw, recordsTotal: logs.recordsTotal, recordsFiltered: logs.recordsFiltered, data: logs.data.items });
                    },
                    error: () => {
                        onError('Failed to load WhatsApp logs.');
                        callback({ draw: request.dtDraw, recordsTotal: 0, recordsFiltered: 0, data: [] });
                    }
                });


            },
            columns: [
                {
                    data: null, title: '#', orderable: false, searchable: false,
                    render: (_: any, __: any, ___: any, meta: any) => meta.row + 1 + meta.settings._iDisplayStart
                },
                { data: 'sendTo', title: 'Mobile Number', render: (d: string) => esc(d) },
                {
                    data: 'message', title: 'Message',
                    render: (d: string) => `<span class="text-break d-inline-block" style="max-width:300px">${esc(d)}</span>`
                },
                {
                    data: 'sendAt', title: 'Sent At',
                    render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
                },
                {
                    data: 'receivedStatus', title: 'Status',
                    visible: !isErrorTab,
                    render: (d: string, t: string, row: any) => {
                        const sendStatus = row.sendStatus;
                        if (t !== 'display' || !d) return d ?? (sendStatus === 1 ? 'Sent' : sendStatus === 2 ? 'Scheduled' : String(sendStatus));
                        return `<span class="badge ${statusBadgeClass(d)}">${esc(d)}</span>`;
                    }
                },
                {
                    data: 'ackId', title: 'Ack ID',
                    visible: !isErrorTab,
                    render: (d: string) => d ? `<span class="text-danger">${esc(d)}</span>` : ''
                },
                {
                    data: 'receivedAt', title: 'Received At',
                    visible: !isErrorTab,
                    render: (d: string, t: string) => (t === 'sort' || t === 'type') ? d : formatDateTime(d)
                },
                {
                    data: 'errorMessage', title: 'Error',
                    render: (d: string) => d ? `<span class="text-danger">${esc(d)}</span>` : ''
                }
            ]
        };
    }
}
