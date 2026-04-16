import { AfterViewInit, Component, ViewChild } from '@angular/core';

import { DatatableHelper } from '../../shared/datatable/datatable.helper';
import { UsersDatatable } from '../../shared/datatable/users-datatable';
import { UserService } from '../../core/services/user.service';
import { UserFormComponent } from './user-form/user-form.component';
import { UserFilterComponent } from './user-filter/user-filter.component';
import { StorageService } from '../../core/services/storage.service';
import { User, UserFilter } from '../../core/models/user.model';
import { ConfirmModalService } from '../../shared/confirm-modal/confirm-modal.service';

declare const $: any;

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements AfterViewInit {
  @ViewChild(UserFormComponent) userForm!: UserFormComponent;
  @ViewChild(UserFilterComponent) userFilter!: UserFilterComponent;

  selectedUser: User | null = null;
  actionType: 'add' | 'edit' | 'credit' | 'endpoints' | 'view' = 'view';
  newPassword = '';
  passwordSubmitting = false;
  passwordError = '';

  private activeFilter: UserFilter = {};
  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly service: UserService,
    private readonly datatableHelper: DatatableHelper,
    private readonly storageService: StorageService,
    private readonly confirmService: ConfirmModalService
  ) { }

  ngAfterViewInit(): void {
    this.initTable();
  }

  private initTable(): void {
    this.datatableHelper.initTable('#usersTable', new UsersDatatable({
      storageService: this.storageService,
      userService: this.service,
      getFilters: () => this.activeFilter,
      callbacks: {
        onEdit: (user) => this.openEditModal(user),
        onToggle: (user) => this.toggleUserEnabled(user),
        onCredits: (user) => this.openCredits(user),
        onPassword: (user) => this.openPasswordModal(user),
        onEndpoints: (user) => this.openEndpoints(user),
        onError: (msg) => this.errorMessage = msg
      }
    }));
  }

  onFilterChange(filter: UserFilter): void {
    this.activeFilter = filter;
    this.datatableHelper.reload('#usersTable');
  }

  reload(): void {
    this.clearMessages();
    this.datatableHelper.reload('#usersTable');
  }
  openEndpoints(user: User): void {
    this.selectedUser = user;
    this.actionType = 'endpoints';
  }

  openAddModal(): void {
    this.selectedUser = null;
    this.clearMessages();
    this.userForm.open();
  }

  openEditModal(user: User): void {
    this.selectedUser = user;
    this.clearMessages();
    this.userForm.open();
  }

  onSaved(): void {
    this.successMessage = this.selectedUser ? 'User updated successfully.' : 'User created successfully.';
    this.selectedUser = null;
    this.actionType = 'view';
    this.datatableHelper.reload('#usersTable');
  }

  toggleUserEnabled(user: User): void {
    const action = user.enabled ? 'disable' : 'enable';
    this.confirmService.confirm({
      title: `${user.enabled ? 'Disable' : 'Enable'} User`,
      message: `Are you sure you want to ${action} "${user.displayName}"?`,
      confirmLabel: user.enabled ? 'Disable' : 'Enable',
      confirmClass: user.enabled ? 'btn-warning' : 'btn-success'
    }).then((confirmed) => {
      if (!confirmed) return;
      this.clearMessages();
      this.service.toggleEnabled(user.id).subscribe({
        next: (updated) => {
          const done = updated.enabled ? 'enabled' : 'disabled';
          this.successMessage = `User "${updated.displayName}" ${done}.`;
          this.datatableHelper.reload('#usersTable');
        },
        error: () => { this.errorMessage = 'Failed to update user status.'; }
      });
    });
  }

  openCredits(user: User): void {
    this.selectedUser = user;
    this.actionType = 'credit';
  }

  openPasswordModal(user: User): void {
    this.selectedUser = user;
    this.newPassword = '';
    this.passwordError = '';
    this.passwordSubmitting = false;
    $('#passwordModal').modal('show');
  }

  submitPasswordUpdate(): void {
    if (!this.selectedUser || !this.newPassword.trim()) return;
    this.passwordSubmitting = true;
    this.passwordError = '';
    this.service.updatePassword(this.selectedUser.id, this.newPassword).subscribe({
      next: () => {
        this.passwordSubmitting = false;
        this.successMessage = `Password updated for "${this.selectedUser!.displayName}".`;
        this.selectedUser = null;
        $('#passwordModal').modal('hide');
      },
      error: () => {
        this.passwordSubmitting = false;
        this.passwordError = 'Failed to update password. Please try again.';
      }
    });
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
  backButton(): void {
    this.selectedUser = null;
    this.actionType = 'view';
  }
}

