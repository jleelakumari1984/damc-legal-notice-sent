import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface ExcelMappingResponse {
  id: number;
  processId: number;
  excelFieldName: string;
  dbFieldName: string;
  isKey: number;
  isMobile: number;
  isMandatory: number;
  isAttachment: number;
  createdAt: string;
}

export interface ExcelMappingRequest {
  processId: number;
  excelFieldName: string;
  dbFieldName: string;
  isKey: number;
  isMobile: number;
  isMandatory: number;
  isAttachment: number;
}

@Injectable({ providedIn: 'root' })
export class ExcelMappingsService {
  private readonly api = `${environment.apiBaseUrl}/excel-mappings`;

  constructor(private readonly http: HttpClient) {}

  getByProcessId(processId: number): Observable<ExcelMappingResponse[]> {
    const params = new HttpParams().set('processId', processId.toString());
    return this.http.get<ExcelMappingResponse[]>(this.api, { params });
  }

  getById(id: number): Observable<ExcelMappingResponse> {
    return this.http.get<ExcelMappingResponse>(`${this.api}/${id}`);
  }

  create(request: ExcelMappingRequest): Observable<ExcelMappingResponse> {
    return this.http.post<ExcelMappingResponse>(this.api, request);
  }

  update(id: number, request: ExcelMappingRequest): Observable<ExcelMappingResponse> {
    return this.http.put<ExcelMappingResponse>(`${this.api}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
