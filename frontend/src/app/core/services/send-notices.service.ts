import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface ProcessExcelMapping {
  id: number;
  excelFieldName: string;
  isMandatory: number;
  isAttachment: number;
  isKey: number;
}

export interface NoticeType {
  id: number;
  name: string;
  excelMap: ProcessExcelMapping[];
}

export interface ValidationRow {
  agreementNumber: string;
  excelData: string;
}

export interface NoticeFileData {
  columnNames: string[];
  rows: ValidationRow[];
}

export interface ValidationResponse {
  scheduleId: number;
  originalZipFile: string;
  extractedFolder: string;
  status: string;
  fileData: NoticeFileData;
}

export interface ExcelPreviewRow {
  data: Record<string, unknown>;
}

export interface ExcelPreview {
  columnNames: string[];
  rows: ExcelPreviewRow[];
}

export interface SendSampleRequest {
  processSno: number;
  mobileNumber: string;
  sendSms: boolean;
  sendWhatsapp: boolean;
  rowData: Record<string, unknown>;
}

export interface ScheduledNotice {
  id: number;
  processSno: number;
  originalFileName: string;
  zipFilePath: string;
  extractedFolderPath: string;
  sendSms: boolean;
  sendWhatsapp: boolean;
  status: string;
  createdAt: string;
}

export interface ScheduledNoticeItem {
  id: number;
  agreementNumber: string;
  excelData: Record<string, unknown>;
  status: string;
  failureReason: string;
  processedAt: string;
  attachements: string;
}

export interface ScheduledNoticeDetail {
  id: number;
  processSno: number;
  originalFileName: string;
  sendSms: boolean;
  sendWhatsapp: boolean;
  status: string;
  createdAt: string;
  processedAt: string;
  failureReason: string;
  items: ScheduledNoticeItem[];
}

@Injectable({ providedIn: 'root' })
export class SendNoticesService {
  private readonly api = `${environment.apiBaseUrl}/notices`;
  private readonly previewApi = `${environment.apiBaseUrl}/excel-preview`;

  constructor(private readonly http: HttpClient) { }

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

  previewExcel(file: File): Observable<ExcelPreview> {
    const formData = new FormData();
    formData.append('zipFile', file);
    return this.http.post<ExcelPreview>(`${this.previewApi}/zip`, formData);
  }

  sendSample(request: SendSampleRequest): Observable<unknown> {
    return this.http.post(`${this.previewApi}/send-sample`, request);
  }

  getScheduledNotices(): Observable<ScheduledNotice[]> {
    return this.http.get<ScheduledNotice[]>(`${this.api}`);
  }

  getScheduledNoticeDetail(id: number): Observable<ScheduledNoticeDetail> {
    return this.http.get<ScheduledNoticeDetail>(`${this.api}/${id}`);
  }
}
