import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Credit, CreditPaginatedRequest, CreditRequest } from '../models/credit.model';
import { PaginatedResponse } from '../models/datatable.model';

@Injectable({ providedIn: 'root' })
export class CreditService {
  private readonly api = `${environment.apiBaseUrl}/users/credits`;

  constructor(private readonly http: HttpClient) { }

  getAll(request?: CreditPaginatedRequest): Observable<PaginatedResponse<Credit[]>> {
    if (!request) {
      request = {} as CreditPaginatedRequest;
      request.allData = true;
    }
    return this.http.post<PaginatedResponse<Credit[]>>(`${this.api}/trans/list`, request);
  }
  create(request: CreditRequest): Observable<Credit> {
    return this.http.post<Credit>(this.api, request);
  }

}
