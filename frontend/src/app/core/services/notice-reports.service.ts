import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NoticeReportDetail, NoticeReportItemDetail, NoticeReportRequest, NoticeReportSummary } from '../models/report.notice';
import { SmsLogResponse, SmsLogRequest, } from '../models/sms.model';
import { WhatsappLogResponse, WhatsappLogRequest } from '../models/whatsapp.model';
import { PaginatedResponse } from '../models/datatable.model';

@Injectable({ providedIn: 'root' })
export class NoticeReportsService {
  private readonly api = `${environment.apiBaseUrl}/reports/notices`;

  constructor(private readonly http: HttpClient) { }

  getAll(request: NoticeReportRequest): Observable<PaginatedResponse<NoticeReportSummary[]>> {
    return this.http.post<PaginatedResponse<NoticeReportSummary[]>>(this.api, request);
  }

  getDetails(id: number, status: string): Observable<NoticeReportDetail> {
    return this.http.get<NoticeReportDetail>(`${this.api}/details/${id}`, { params: { status } });
  }

  getItemDetail(itemId: number): Observable<NoticeReportItemDetail> {
    return this.http.get<NoticeReportItemDetail>(`${this.api}/items/${itemId}`);
  }

  getSmsLogs(reportId: number, request: SmsLogRequest): Observable<PaginatedResponse<SmsLogResponse>> {
    if (request.status === 'error') {
      return this.getErrorSmsLogs(reportId, request);
    }
    return this.http.post<PaginatedResponse<SmsLogResponse>>(`${this.api}/sms-logs/${reportId}`, request);
  }
  getErrorSmsLogs(reportId: number, request: SmsLogRequest): Observable<PaginatedResponse<SmsLogResponse>> {
    return this.http.post<PaginatedResponse<SmsLogResponse>>(`${this.api}/error-sms-logs/${reportId}`, request);
  }
  getWhatsappLogs(reportId: number, request: WhatsappLogRequest): Observable<PaginatedResponse<WhatsappLogResponse>> {
    if (request.status === 'error') {
      return this.getErrorWhatsappLogs(reportId, request);
    }
    return this.http.post<PaginatedResponse<WhatsappLogResponse>>(`${this.api}/whats-app-logs/${reportId}`, request);
  }
  getErrorWhatsappLogs(reportId: number, request: WhatsappLogRequest): Observable<PaginatedResponse<WhatsappLogResponse>> {
    return this.http.post<PaginatedResponse<WhatsappLogResponse>>(`${this.api}/error-whats-app-logs/${reportId}`, request);
  }

  exportSmsLogs(reportId: number, request: SmsLogRequest): Observable<Blob> {
    return this.http.post(`${this.api}/download-sms-logs/${reportId}`, request, {
      responseType: 'blob'
    });
  }
  exportErrorSmsLogs(reportId: number, request: SmsLogRequest): Observable<Blob> {
    return this.http.post(`${this.api}/download-error-sms-logs/${reportId}`, request, {
      responseType: 'blob'
    });
  }

  exportWhatsappLogs(reportId: number, request: WhatsappLogRequest): Observable<Blob> {
    if (request.status === 'error') {
      return this.exportErrorWhatsappLogs(reportId, request);
    }
    return this.http.post(`${this.api}/download-whats-app-logs/${reportId}`, request, {
      responseType: 'blob'
    });
  }
  exportErrorWhatsappLogs(reportId: number, request: WhatsappLogRequest): Observable<Blob> {
    return this.http.post(`${this.api}/download-error-whats-app-logs/${reportId}`, request, {
      responseType: 'blob'
    });
  }
}
