import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { StorageService } from '../storage.service';

interface LoginRequest {
  username: string;
  password: string;
}
export interface UserResponse {
  id: number;
  displayName: string;
  loginName: string;
  userEmail: string;
  accessLevel: number;
}
export interface LoginResponse {
  accessToken: string;
  user: UserResponse;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;

  constructor(private readonly http: HttpClient, private readonly storageService: StorageService) { }

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((response) => {
        this.storageService.setSession(response.accessToken, response.user);
      })
    );
  }

  loginAs(userId: number): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login-as/${userId}`, {});
  }
}
