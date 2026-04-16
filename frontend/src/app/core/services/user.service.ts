import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, UserFilter, UserRequest } from '../models/user.model';
import { PaginatedRequest, PaginatedResponse } from '../models/datatable.model';



@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly api = `${environment.apiBaseUrl}/users`;

  constructor(private readonly http: HttpClient) { }

  getAll(request?: PaginatedRequest<UserFilter>): Observable<PaginatedResponse<User[]>> {
    if (!request) {
      request = {} as PaginatedRequest<UserFilter>;
      request.filter = {} as UserFilter;
      request.allData = true;
    }
    return this.http.post<PaginatedResponse<User[]>>(`${this.api}/list`, request);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/${id}`);
  }

  create(request: UserRequest): Observable<User> {
    return this.http.post<User>(`${this.api}`, request);
  }

  update(id: number, request: UserRequest): Observable<User> {
    return this.http.put<User>(`${this.api}/${id}`, request);
  }

  updatePassword(id: number, password: string): Observable<User> {
    var req = { password: password };
    return this.http.patch<User>(`${this.api}/${id}/password`, req);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  toggleEnabled(id: number): Observable<User> {
    return this.http.patch<User>(`${this.api}/${id}/toggle-enabled`, {});
  }
}
