import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface NoticeType {
  id: number;
  name: string;
}

export interface ValidationRow {
  agreementNumber: string;
  customerName: string;
  mobileSms: string;
  mobileWhatsapp: string;
  expectedPdfFile: string;
  documentPresent: boolean;
}

export interface ValidationResponse {
  scheduleId: number;
  originalZipFile: string;
  extractedFolder: string;
  status: string;
  rows: ValidationRow[];
}

@Injectable({ providedIn: 'root' })
export class SendNoticesService {
  private readonly api = 'http://localhost:8080/api/notices';

  constructor(private readonly http: HttpClient) {}

  getNoticeTypes(): Observable<NoticeType[]> {
    return this.http.get<NoticeType[]>(`${this.api}/types`);
  }

  scheduleNotice(processSno: number, sendSms: boolean, sendWhatsapp: boolean, file: File): Observable<ValidationResponse> {
    const formData = new FormData();
    formData.append('processSno', processSno.toString());
    formData.append('sendSms', String(sendSms));
    formData.append('sendWhatsapp', String(sendWhatsapp));
    formData.append('zipFile', file);
    return this.http.post<ValidationResponse>(`${this.api}/schedule`, formData);
  }
}
