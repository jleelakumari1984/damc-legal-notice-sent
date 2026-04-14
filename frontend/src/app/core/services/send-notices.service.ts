import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ExcelPreview, ValidationResponse } from '../models/excel.model';
import { SendSampleRequest } from '../models/schedule.model';



@Injectable({ providedIn: 'root' })
export class SendNoticesService {
  private readonly api = `${environment.apiBaseUrl}/notices`;
  private readonly previewApi = `${environment.apiBaseUrl}/excel-preview`;

  constructor(private readonly http: HttpClient) { }


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

}
