import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NoticeExcelMappingRequest, NoticeExcelMappingResponse } from '../models/notices.model';

@Injectable({ providedIn: 'root' })
export class NoticeExcelMappingsService {
  private readonly api = `${environment.apiBaseUrl}/notice-mappings/excel`;

  constructor(private readonly http: HttpClient) { }

  getByProcessId(processId: number): Observable<NoticeExcelMappingResponse[]> {
    const params = new HttpParams().set('processId', processId.toString());
    return this.http.get<NoticeExcelMappingResponse[]>(this.api, { params });
  }

  getById(id: number): Observable<NoticeExcelMappingResponse> {
    return this.http.get<NoticeExcelMappingResponse>(`${this.api}/${id}`);
  }

  create(request: NoticeExcelMappingRequest): Observable<NoticeExcelMappingResponse> {
    return this.http.post<NoticeExcelMappingResponse>(this.api, request);
  }

  update(id: number, request: NoticeExcelMappingRequest): Observable<NoticeExcelMappingResponse> {
    return this.http.put<NoticeExcelMappingResponse>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
