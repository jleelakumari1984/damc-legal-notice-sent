import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { StorageService } from '../../core/services/storage.service';
import { BaseComponent } from '../../shared/base/base.component';

declare const $: any;

@Component({
  selector: 'app-auth-layout',
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.css']
})
export class AuthLayoutComponent extends BaseComponent {
  userName: string | undefined = undefined;

  constructor(private readonly router: Router) {
    super();
    this.userName = this.currentUser?.displayName;
  }

  canChangeSession(): boolean {
    return this.isSuperOrAdmin() || this.currentUser?.canSwitchSession || false;
  }

  openSwitchSession(): void {
    $('#switchSessionModal').modal('show');
  }

  logout(): void {
    this.storageService.logout();
    this.router.navigate(['/login']);
  }
}

