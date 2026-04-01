import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  accessToken: string;
  user: {
    id: number;
    displayName: string;
    loginName: string;
    userEmail: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly tokenKey = 'damc_token';

  constructor(private readonly http: HttpClient) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((response) => {
        localStorage.setItem(this.tokenKey, response.accessToken);
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }
}
