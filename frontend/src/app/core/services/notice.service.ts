import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notice, NoticeRequest, NoticeType } from '../models/notices.model';
import { NoticeReportRequest } from '../models/report.notice';
import { PaginatedResponse } from '../models/datatable.model';


@Injectable({ providedIn: 'root' })
export class NoticeService {
  private readonly api = `${environment.apiBaseUrl}/notices`;

  constructor(private readonly http: HttpClient) { }

  getNoticeTypes(request?: NoticeReportRequest): Observable<PaginatedResponse<NoticeType[]>> {
    if (!request) {
      request = {} as NoticeReportRequest;
      request.allData = true;
    }
    return this.http.post<PaginatedResponse<NoticeType[]>>(`${this.api}/types`, request);
  }
  getById(id: number): Observable<Notice> {
    return this.http.get<Notice>(`${this.api}/${id}`);
  }

  create(request: NoticeRequest): Observable<Notice> {
    return this.http.post<Notice>(this.api, request);
  }

  update(id: number, request: NoticeRequest): Observable<Notice> {
    return this.http.put<Notice>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  retry(id: number): Observable<void> {
    return this.http.post<void>(`${this.api}/${id}/retry`, {});
  }

  cancel(id: number): Observable<void> {
    return this.http.post<void>(`${this.api}/${id}/cancel`, {});
  }
}
