import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SmsPendingTemplate, SmsPendingTemplateRequest, SmsTemplate, SmsTemplateRequest, SmsUserTemplateRequest, WhatsappPendingTemplate, WhatsappPendingTemplateRequest, WhatsappTemplate, WhatsappTemplateRequest, WhatsappUserTemplateRequest } from '../models/notices.model';
import { PaginatedRequest, PaginatedResponse } from '../models/datatable.model';

export interface ApproveTemplateRequest {
  peid?: string;
  senderId?: string;
  routeId?: string;
  templateContent: string;
  templateId?: string;
  channel?: string;
  dcs?: number;
  flashSms?: number;
  templateName?: string;
  templateLang?: string;
  templatePath?: string;
}

export interface RejectTemplateRequest {
  rejectionReason: string;
}

@Injectable({ providedIn: 'root' })
export class NoticeTemplateService {
  private readonly api = `${environment.apiBaseUrl}/notice-mappings`;

  constructor(private readonly http: HttpClient) { }

  // ── SMS Templates ────────────────────────────────────────────────────────────

  getSmsTemplates(noticeId: number): Observable<SmsTemplate[]> {
    const params = new HttpParams().set('noticeId', noticeId.toString());
    return this.http.get<SmsTemplate[]>(`${this.api}/sms/list`, { params });
  }

  createSmsTemplate(request: SmsUserTemplateRequest): Observable<SmsTemplate> {
    return this.http.post<SmsTemplate>(`${this.api}/sms`, request);
  }

  updateSmsTemplate(id: number, request: SmsUserTemplateRequest): Observable<SmsTemplate> {
    return this.http.put<SmsTemplate>(`${this.api}/sms/${id}`, request);
  }

  getPendingSmsTemplates(request: PaginatedRequest<SmsPendingTemplateRequest>): Observable<PaginatedResponse<SmsPendingTemplate[]>> {
    return this.http.post<PaginatedResponse<SmsPendingTemplate[]>>(`${this.api}/sms/pending`, request);
  }

  approveSmsTemplate(id: number, request: ApproveTemplateRequest): Observable<SmsTemplate> {
    return this.http.patch<SmsTemplate>(`${this.api}/sms/approve/${id}`, request);
  }

  rejectSmsTemplate(id: number, request: RejectTemplateRequest): Observable<SmsTemplate> {
    return this.http.patch<SmsTemplate>(`${this.api}/sms/reject/${id}`, request);
  }

  // ── WhatsApp Templates ───────────────────────────────────────────────────────

  getWhatsappTemplates(noticeId: number): Observable<WhatsappTemplate[]> {
    const params = new HttpParams().set('noticeId', noticeId.toString());
    return this.http.get<WhatsappTemplate[]>(`${this.api}/whatsapp/list`, { params });
  }

  createWhatsappTemplate(request: WhatsappUserTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.post<WhatsappTemplate>(`${this.api}/whatsapp`, request);
  }

  updateWhatsappTemplate(id: number, request: WhatsappUserTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.put<WhatsappTemplate>(`${this.api}/whatsapp/${id}`, request);
  }

  getPendingWhatsappTemplates(request: PaginatedRequest<WhatsappPendingTemplateRequest>): Observable<PaginatedResponse<WhatsappPendingTemplate[]>> {
    return this.http.post<PaginatedResponse<WhatsappPendingTemplate[]>>(`${this.api}/whatsapp/pending`, request);
  }

  approveWhatsappTemplate(id: number, request: ApproveTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.patch<WhatsappTemplate>(`${this.api}/whatsapp/approve/${id}`, request);
  }

  rejectWhatsappTemplate(id: number, request: RejectTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.patch<WhatsappTemplate>(`${this.api}/whatsapp/reject/${id}`, request);
  }

  toggleSmsTemplateStatus(id: number): Observable<SmsTemplate> {
    return this.http.patch<SmsTemplate>(`${this.api}/sms/toggle-status/${id}`, {});
  }

  toggleWhatsappTemplateStatus(id: number): Observable<WhatsappTemplate> {
    return this.http.patch<WhatsappTemplate>(`${this.api}/whatsapp/toggle-status/${id}`, {});
  }
}
