import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class LoginService {
  constructor(private readonly authService: AuthService) {}

  login(username: string, password: string): Observable<unknown> {
    return this.authService.login({ username, password });
  }
}
