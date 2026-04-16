import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { UserSmsCredential, UserSmsCredentialRequest } from '../models/endpoint.model';

@Injectable({ providedIn: 'root' })
export class UserSmsCredentialService {
  private readonly api = `${environment.apiBaseUrl}/users/endpoint/sms`;

  constructor(private readonly http: HttpClient) {}

  get(userId: number): Observable<UserSmsCredential> {
    return this.http.get<UserSmsCredential>(`${this.api}/${userId}`);
  }

  save(userId: number, request: UserSmsCredentialRequest): Observable<UserSmsCredential> {
    return this.http.put<UserSmsCredential>(`${this.api}/${userId}`, request);
  }
}
