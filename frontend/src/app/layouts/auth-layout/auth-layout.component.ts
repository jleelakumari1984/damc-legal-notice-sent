import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth/auth.service';

@Component({
  selector: 'app-auth-layout',
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.css']
})
export class AuthLayoutComponent {
  userName: string | undefined = undefined;

  constructor(private readonly authService: AuthService, private readonly router: Router) {
    this.userName = this.authService.getUser()?.displayName;

  }

  isSuperAdmin(): boolean {
    return this.authService.isSuperAdmin();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
