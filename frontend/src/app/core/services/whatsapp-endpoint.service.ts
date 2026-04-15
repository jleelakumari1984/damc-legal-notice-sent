import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { WhatsappEndpoint, WhatsappEndpointRequest } from '../models/endpoint.model';

@Injectable({ providedIn: 'root' })
export class WhatsappEndpointService {
  private readonly api = `${environment.apiBaseUrl}/whatsapp-endpoints`;

  constructor(private readonly http: HttpClient) { }

  getAll(): Observable<WhatsappEndpoint[]> {
    return this.http.get<WhatsappEndpoint[]>(this.api);
  }

  create(request: WhatsappEndpointRequest): Observable<WhatsappEndpoint> {
    return this.http.post<WhatsappEndpoint>(this.api, request);
  }

  update(id: number, request: WhatsappEndpointRequest): Observable<WhatsappEndpoint> {
    return this.http.put<WhatsappEndpoint>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
