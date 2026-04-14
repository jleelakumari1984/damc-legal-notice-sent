import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface User {
  id: number;
  name: string;
  email: string;
  mobile: string;
  role: string;
  status: string;
  createdAt: string;
}

export interface UserRequest {
  name: string;
  email: string;
  mobile: string;
  role: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly api = `${environment.apiBaseUrl}/users`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.api);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/${id}`);
  }

  create(request: UserRequest): Observable<User> {
    return this.http.post<User>(this.api, request);
  }

  update(id: number, request: UserRequest): Observable<User> {
    return this.http.put<User>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
