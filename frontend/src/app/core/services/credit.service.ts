import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Credit, CreditRequest } from '../models/credit.model';

@Injectable({ providedIn: 'root' })
export class CreditService {
  private readonly api = `${environment.apiBaseUrl}/credits`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Credit[]> {
    return this.http.get<Credit[]>(this.api);
  }

  getById(id: number): Observable<Credit> {
    return this.http.get<Credit>(`${this.api}/${id}`);
  }

  create(request: CreditRequest): Observable<Credit> {
    return this.http.post<Credit>(this.api, request);
  }

  update(id: number, request: CreditRequest): Observable<Credit> {
    return this.http.put<Credit>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
