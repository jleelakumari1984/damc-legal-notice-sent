import { Injectable } from '@angular/core';

import { LoginResponse, UserResponse } from './auth/auth.service';

@Injectable({ providedIn: 'root' })
export class StorageService {
  private readonly tokenKey = 'legal_notice_token';
  private readonly userKey = 'legal_notice_user';
  private readonly originalTokenKey = 'legal_notice_original_token';
  private readonly originalUserKey = 'legal_notice_original_user';

  setSession(token: string, user: UserResponse): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(user));
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
    return this.getUser()?.accessLevel === 1;
  }

  isAdmin(): boolean {
    return this.getUser()?.accessLevel === 2;
  }

  isUser(): boolean {
    return this.getUser()?.accessLevel === 3;
  }

  isSuperOrAdmin(): boolean {
    const level = this.getUser()?.accessLevel;
    return level === 1 || level === 2;
  }

  getToken(): string | null {
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

  isInSwitchedSession(): boolean {
    return !!localStorage.getItem(this.originalTokenKey);
  }

  getOriginalUser(): UserResponse | null {
    const userJson = localStorage.getItem(this.originalUserKey);
    if (userJson) {
      try {
        return JSON.parse(userJson) as UserResponse;
      } catch {
        return null;
      }
    }
    return null;
  }

  switchToSession(response: LoginResponse): void {
    const currentToken = localStorage.getItem(this.tokenKey);
    const currentUser = localStorage.getItem(this.userKey);
    if (currentToken) localStorage.setItem(this.originalTokenKey, currentToken);
    if (currentUser) localStorage.setItem(this.originalUserKey, currentUser);
    localStorage.setItem(this.tokenKey, response.accessToken);
    localStorage.setItem(this.userKey, JSON.stringify(response.user));
  }

  switchBack(): void {
    const originalToken = localStorage.getItem(this.originalTokenKey);
    const originalUser = localStorage.getItem(this.originalUserKey);
    if (originalToken) localStorage.setItem(this.tokenKey, originalToken);
    if (originalUser) localStorage.setItem(this.userKey, originalUser);
    localStorage.removeItem(this.originalTokenKey);
    localStorage.removeItem(this.originalUserKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    localStorage.removeItem(this.originalTokenKey);
    localStorage.removeItem(this.originalUserKey);
  }
}
