import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SmsPendingTemplateResponse, SmsPendingTemplateFilter, SmsTemplate, SmsUserTemplateRequest, WhatsappPendingTemplateResponse, WhatsappPendingTemplateFilter, WhatsappTemplate, WhatsappUserTemplateRequest, WhatsappRejectTemplateRequest, WhatsappApproveTemplateRequest, SmsRejectTemplateRequest, SmsApproveTemplateRequest } from '../models/notices.model';
import { PaginatedRequest, PaginatedResponse } from '../models/datatable.model';

@Injectable({ providedIn: 'root' })
export class NoticeTemplateService {
  private readonly api = `${environment.apiBaseUrl}/notice-mappings`;

  constructor(private readonly http: HttpClient) { }

  // ── SMS Templates ────────────────────────────────────────────────────────────

  getSmsTemplates(noticeId: number, status: boolean | null): Observable<SmsTemplate[]> {
    let params = new HttpParams().set('noticeId', noticeId.toString());
    if (status !== null) {
      params = params.set('status', status.toString());
    }
    return this.http.get<SmsTemplate[]>(`${this.api}/sms/list`, { params });
  }

  createSmsTemplate(request: SmsUserTemplateRequest): Observable<SmsTemplate> {
    return this.http.post<SmsTemplate>(`${this.api}/sms`, request);
  }

  updateSmsTemplate(id: number, request: SmsUserTemplateRequest): Observable<SmsTemplate> {
    return this.http.put<SmsTemplate>(`${this.api}/sms/${id}`, request);
  }

  getPendingSmsTemplates(request: PaginatedRequest<SmsPendingTemplateFilter>): Observable<PaginatedResponse<SmsPendingTemplateResponse[]>> {
    return this.http.post<PaginatedResponse<SmsPendingTemplateResponse[]>>(`${this.api}/sms/pending`, request);
  }

  approveSmsTemplate(id: number, request: SmsApproveTemplateRequest): Observable<SmsTemplate> {
    return this.http.patch<SmsTemplate>(`${this.api}/sms/approve/${id}`, request);
  }

  rejectSmsTemplate(id: number, request: SmsRejectTemplateRequest): Observable<SmsTemplate> {
    return this.http.patch<SmsTemplate>(`${this.api}/sms/reject/${id}`, request);
  }

  // ── WhatsApp Templates ───────────────────────────────────────────────────────

  getWhatsappTemplates(noticeId: number, status: boolean | null): Observable<WhatsappTemplate[]> {
    let params = new HttpParams().set('noticeId', noticeId.toString());
    if (status !== null) {
      params = params.set('status', status.toString());
    }
    return this.http.get<WhatsappTemplate[]>(`${this.api}/whatsapp/list`, { params });
  }

  createWhatsappTemplate(request: WhatsappUserTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.post<WhatsappTemplate>(`${this.api}/whatsapp`, request);
  }

  updateWhatsappTemplate(id: number, request: WhatsappUserTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.put<WhatsappTemplate>(`${this.api}/whatsapp/${id}`, request);
  }

  getPendingWhatsappTemplates(request: PaginatedRequest<WhatsappPendingTemplateFilter>): Observable<PaginatedResponse<WhatsappPendingTemplateResponse[]>> {
    return this.http.post<PaginatedResponse<WhatsappPendingTemplateResponse[]>>(`${this.api}/whatsapp/pending`, request);
  }

  approveWhatsappTemplate(id: number, request: WhatsappApproveTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.patch<WhatsappTemplate>(`${this.api}/whatsapp/approve/${id}`, request);
  }

  rejectWhatsappTemplate(id: number, request: WhatsappRejectTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.patch<WhatsappTemplate>(`${this.api}/whatsapp/reject/${id}`, request);
  }

  toggleSmsTemplateStatus(id: number): Observable<SmsTemplate> {
    return this.http.patch<SmsTemplate>(`${this.api}/sms/toggle-status/${id}`, {});
  }

  toggleWhatsappTemplateStatus(id: number): Observable<WhatsappTemplate> {
    return this.http.patch<WhatsappTemplate>(`${this.api}/whatsapp/toggle-status/${id}`, {});
  }
}
