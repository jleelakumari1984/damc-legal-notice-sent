import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SmsTemplate, SmsTemplateRequest, SmsUserTemplateRequest, WhatsappTemplate, WhatsappTemplateRequest, WhatsappUserTemplateRequest } from '../models/notices.model';

@Injectable({ providedIn: 'root' })
export class NoticeTemplateService {
  private readonly api = `${environment.apiBaseUrl}/notices`;

  constructor(private readonly http: HttpClient) { }

  // ── SMS Templates ────────────────────────────────────────────────────────────

  getSmsTemplates(processId: number): Observable<SmsTemplate[]> {
    return this.http.get<SmsTemplate[]>(`${this.api}/${processId}/sms-templates`);
  }

  createSmsTemplate(request: SmsUserTemplateRequest): Observable<SmsTemplate> {
    return this.http.post<SmsTemplate>(`${this.api}/sms-templates`, request);
  }

  updateSmsTemplate(id: number, request: SmsUserTemplateRequest): Observable<SmsTemplate> {
    return this.http.put<SmsTemplate>(`${this.api}/sms-templates/${id}`, request);
  }

  deleteSmsTemplate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/sms-templates/${id}`);
  }

  // ── WhatsApp Templates ───────────────────────────────────────────────────────

  getWhatsappTemplates(processId: number): Observable<WhatsappTemplate[]> {
    return this.http.get<WhatsappTemplate[]>(`${this.api}/${processId}/whatsapp-templates`);
  }

  createWhatsappTemplate(request: WhatsappUserTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.post<WhatsappTemplate>(`${this.api}/whatsapp-templates`, request);
  }

  updateWhatsappTemplate(id: number, request: WhatsappUserTemplateRequest): Observable<WhatsappTemplate> {
    return this.http.put<WhatsappTemplate>(`${this.api}/whatsapp-templates/${id}`, request);
  }

  deleteWhatsappTemplate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/whatsapp-templates/${id}`);
  }
}
