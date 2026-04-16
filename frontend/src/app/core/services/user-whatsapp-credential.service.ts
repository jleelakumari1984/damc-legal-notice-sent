import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { UserWhatsAppCredential, UserWhatsAppCredentialRequest } from '../models/endpoint.model';

@Injectable({ providedIn: 'root' })
export class UserWhatsAppCredentialService {
  private readonly api = `${environment.apiBaseUrl}/users/endpoint/whatsapp`;

  constructor(private readonly http: HttpClient) {}

  get(userId: number): Observable<UserWhatsAppCredential> {
    return this.http.get<UserWhatsAppCredential>(`${this.api}/${userId}`);
  }

  save(userId: number, request: UserWhatsAppCredentialRequest): Observable<UserWhatsAppCredential> {
    return this.http.put<UserWhatsAppCredential>(`${this.api}/${userId}`, request);
  }
}
