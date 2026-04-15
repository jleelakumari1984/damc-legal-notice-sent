import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';

import { StorageService } from '../services/storage.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly storageService: StorageService) { }

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.storageService.getToken();
    const tokenExpired = this.storageService.isTokenExpired();
    if (!token || tokenExpired) {
      return next.handle(req);
    }

    const cloned = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });

    return next.handle(cloned).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error.status === 401) {
      // this.storageService.logout();
    }
    return throwError(() => error);
  }
}
