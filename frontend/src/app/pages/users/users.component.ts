import { AfterViewInit, Component, ViewChild } from '@angular/core';

import { DatatableHelper } from '../../shared/datatable/datatable.helper';
import { UsersDatatable } from '../../shared/datatable/users-datatable';
import { User, UserService } from '../../core/services/user.service';
import { UserFormComponent } from './user-form/user-form.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements AfterViewInit {
  @ViewChild(UserFormComponent) userForm!: UserFormComponent;

  editUser: User | null = null;
  deletingId: number | null = null;

  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly service: UserService,
    private readonly datatableHelper: DatatableHelper
  ) {}

  ngAfterViewInit(): void {
    this.initTable();
  }

  private initTable(): void {
    this.datatableHelper.initTable('#usersTable', new UsersDatatable({
      onEdit: (user) => this.openEditModal(user),
      onDelete: (user) => this.confirmDelete(user)
    }));
  }

  reload(): void {
    this.clearMessages();
    this.datatableHelper.reload('#usersTable');
  }

  openAddModal(): void {
    this.editUser = null;
    this.clearMessages();
    this.userForm.open();
  }

  openEditModal(user: User): void {
    this.editUser = user;
    this.clearMessages();
    this.userForm.open();
  }

  onSaved(): void {
    this.successMessage = this.editUser ? 'User updated successfully.' : 'User created successfully.';
    this.editUser = null;
    this.datatableHelper.reload('#usersTable');
  }

  confirmDelete(user: User): void {
    if (!confirm(`Delete user "${user.name}"?`)) return;
    this.deletingId = user.id;
    this.clearMessages();
    this.service.delete(user.id).subscribe({
      next: () => {
        this.deletingId = null;
        this.successMessage = `User "${user.name}" deleted.`;
        this.datatableHelper.reload('#usersTable');
      },
      error: () => {
        this.deletingId = null;
        this.errorMessage = 'Failed to delete user.';
      }
    });
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}

