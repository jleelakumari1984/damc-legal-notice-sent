import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../../environments/environment';

interface LoginRequest {
  username: string;
  password: string;
}
interface UserResponse {
  id: number;
  displayName: string;
  loginName: string;
  userEmail: string;
  accessLevel: number;
}
interface LoginResponse {
  accessToken: string;
  user: UserResponse;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;
  private readonly tokenKey = 'legal_notice_token';
  private readonly userKey = 'legal_notice_user';

  accessToken: string | null = null;
  user: UserResponse | null = null;
  constructor(private readonly http: HttpClient) { }


  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((response) => {
        this.accessToken = response.accessToken;
        this.user = response.user;
        localStorage.setItem(this.tokenKey, response.accessToken);
        localStorage.setItem(this.userKey, JSON.stringify(response.user));

      })
    );
  }
  getUser(): UserResponse | null {
    const userJson = localStorage.getItem(this.userKey);
    if (userJson) {
      try {
        return JSON.parse(userJson) as UserResponse;
      } catch {
        return null;
      }
    }
    return null;
  }

  isSuperAdmin(): boolean {
    console.log('Checking super admin access for user:', this.user, this.accessToken);
    return this.getUser()?.accessLevel === 1;
  }

  getToken(): string | null {
    //   console.log('Getting token:', this.accessToken);
    return localStorage.getItem(this.tokenKey);
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) {
      return true;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiryMs = payload.exp * 1000;
      const expired = Date.now() >= expiryMs;
      if (expired) {
        this.logout();
      }
      return expired;
    } catch {
      this.logout();
      return true;
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken() && !this.isTokenExpired() && !!this.getUser();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }
}
