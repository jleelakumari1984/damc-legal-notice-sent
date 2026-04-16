import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { StorageService } from '../../core/services/storage.service';

declare const $: any;

@Component({
  selector: 'app-auth-layout',
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.css']
})
export class AuthLayoutComponent {
  userName: string | undefined = undefined;

  constructor(private readonly storageService: StorageService, private readonly router: Router) {
    this.userName = this.storageService.getUser()?.displayName;
  }

  isSuperAdmin(): boolean {
    return this.storageService.isSuperAdmin();
  }

  isSuperOrAdmin(): boolean {
    return this.storageService.isSuperOrAdmin();
  }
  canChangeSession(): boolean {
    return this.storageService.isSuperOrAdmin() || this.storageService.getUser()?.canSwitchSession || false;
  }

  openSwitchSession(): void {
    $('#switchSessionModal').modal('show');
  }

  logout(): void {
    this.storageService.logout();
    this.router.navigate(['/login']);
  }
}

