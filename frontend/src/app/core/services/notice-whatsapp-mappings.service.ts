import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NoticeExcelMappingRequest, NoticeExcelMappingResponse } from '../models/notices.model';

@Injectable({ providedIn: 'root' })
export class NoticeWhatsappMappingsService {
  private readonly api = `${environment.apiBaseUrl}/notice-mappings/whatsapp`;

  constructor(private readonly http: HttpClient) { }

  getByProcessId(processId: number): Observable<NoticeExcelMappingResponse[]> {
    const params = new HttpParams().set('processId', processId.toString());
    return this.http.get<NoticeExcelMappingResponse[]>(this.api, { params });
  }

  getById(id: number): Observable<NoticeExcelMappingResponse> {
    return this.http.get<NoticeExcelMappingResponse>(`${this.api}/${id}`);
  }

}
