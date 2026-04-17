import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NoticeRequest, NoticeType } from '../models/notices.model';
import { NoticeReportFilter } from '../models/report.notice';
import { PaginatedRequest, PaginatedResponse } from '../models/datatable.model';


@Injectable({ providedIn: 'root' })
export class NoticeService {
  private readonly api = `${environment.apiBaseUrl}/notices`;

  constructor(private readonly http: HttpClient) { }

  getMyNoticeTypes(request?: PaginatedRequest<NoticeReportFilter>): Observable<PaginatedResponse<NoticeType[]>> {
    if (!request) {
      request = {} as PaginatedRequest<NoticeReportFilter>;
      request.filter = {};
      request.allData = true;
    }
    return this.http.post<PaginatedResponse<NoticeType[]>>(`${this.api}/types/list/me`, request);
  }
  getNoticeTypes(request?: PaginatedRequest<NoticeReportFilter>): Observable<PaginatedResponse<NoticeType[]>> {
    if (!request) {
      request = {} as PaginatedRequest<NoticeReportFilter>;
      request.filter = {};
      request.allData = true;
    }
    return this.http.post<PaginatedResponse<NoticeType[]>>(`${this.api}/types/list`, request);
  }
  getById(id: number): Observable<NoticeType> {
    return this.http.get<NoticeType>(`${this.api}/${id}`);
  }

  create(request: NoticeRequest): Observable<NoticeType> {
    return this.http.post<NoticeType>(`${this.api}/types/create`, request);
  }

  update(id: number, request: NoticeRequest): Observable<NoticeType> {
    return this.http.put<NoticeType>(`${this.api}/types/${id}`, request);
  }

  retry(id: number): Observable<void> {
    return this.http.post<void>(`${this.api}/${id}/retry`, {});
  }

  cancel(id: number): Observable<void> {
    return this.http.post<void>(`${this.api}/${id}/cancel`, {});
  }
}
