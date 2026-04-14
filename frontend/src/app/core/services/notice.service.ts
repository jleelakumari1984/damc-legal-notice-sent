import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notice, NoticeRequest, NoticeType } from '../models/notices.model';


@Injectable({ providedIn: 'root' })
export class NoticeService {
  private readonly api = `${environment.apiBaseUrl}/notices`;

  constructor(private readonly http: HttpClient) { }
 
  getNoticeTypes(): Observable<NoticeType[]> {
    return this.http.get<NoticeType[]>(`${this.api}/types`);
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
