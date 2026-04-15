import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SmsEndpoint, SmsEndpointRequest } from '../models/endpoint.model';


@Injectable({ providedIn: 'root' })
export class SmsEndpointService {
  private readonly api = `${environment.apiBaseUrl}/sms-endpoints`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<SmsEndpoint[]> {
    return this.http.get<SmsEndpoint[]>(this.api);
  }

  create(request: SmsEndpointRequest): Observable<SmsEndpoint> {
    return this.http.post<SmsEndpoint>(this.api, request);
  }

  update(id: number, request: SmsEndpointRequest): Observable<SmsEndpoint> {
    return this.http.put<SmsEndpoint>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
