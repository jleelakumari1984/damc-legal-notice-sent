import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NoticeReportDetail, NoticeReportItemDetail, NoticeReportSummary } from '../models/report.notice';

@Injectable({ providedIn: 'root' })
export class NoticeReportsService {
  private readonly api = `${environment.apiBaseUrl}/reports/notices`;

  constructor(private readonly http: HttpClient) { }

  getAll(): Observable<NoticeReportSummary[]> {
    return this.http.get<NoticeReportSummary[]>(this.api);
  }

  getById(id: number): Observable<NoticeReportDetail> {
    return this.http.get<NoticeReportDetail>(`${this.api}/${id}`);
  }

  getItemDetail(itemId: number): Observable<NoticeReportItemDetail> {
    return this.http.get<NoticeReportItemDetail>(`${this.api}/items/${itemId}`);
  }
}
