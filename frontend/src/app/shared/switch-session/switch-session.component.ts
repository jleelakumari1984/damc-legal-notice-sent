import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth/auth.service';
import { StorageService } from '../../core/services/storage.service';
import { UserResponse } from '../../core/services/auth/auth.service';
import { UserService } from '../../core/services/user.service';
import { User, UserFilter } from '../../core/models/user.model';
import { PaginatedRequest } from '../../core/models/datatable.model';
import { BaseComponent } from '../base/base.component';

declare const $: any;

@Component({
  selector: 'app-switch-session',
  templateUrl: './switch-session.component.html'
})
export class SwitchSessionComponent extends BaseComponent implements OnInit {
  isInSwitchedSession = false;
  originalUser: UserResponse | null = null;

  users: User[] = [];
  selectedUserId: number | null = null;
  loading = false;
  switching = false;
  errorMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly userService: UserService,
    private readonly router: Router
  ) { super(); }

  ngOnInit(): void {
    this.isInSwitchedSession = this.storageService.isInSwitchedSession();
    this.originalUser = this.storageService.getOriginalUser();
  }

  refresh(): void {
    this.isInSwitchedSession = this.storageService.isInSwitchedSession();
    this.originalUser = this.storageService.getOriginalUser();
    this.errorMessage = '';
    this.selectedUserId = null;

    if (!this.isInSwitchedSession) {
      this.loading = true;
      let request = { filter: { enabled: true }, allData: true } as PaginatedRequest<UserFilter>;
      this.userService.getAll(request).subscribe({
        next: (users) => { this.users = users.data; this.loading = false; },
        error: () => { this.errorMessage = 'Failed to load users.'; this.loading = false; }
      });
    }
  }

  switchTo(): void {
    if (!this.selectedUserId) return;
    this.switching = true;
    this.errorMessage = '';
    this.authService.loginAs(this.selectedUserId).subscribe({
      next: (response) => {
        this.switching = false;
        $('#switchSessionModal').modal('hide');
        this.router.navigate(['/notice']);
        window.location.reload();
      },
      error: () => {
        this.switching = false;
        this.errorMessage = 'Failed to switch session.';
      }
    });
  }

  switchBack(): void {
    this.storageService.switchBack();
    $('#switchSessionModal').modal('hide');
    this.router.navigate(['/notice']);
    window.location.reload();
  }
}

