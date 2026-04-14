import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { User, UserRequest, UserService } from '../../../core/services/user.service';

declare const $: any;

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html'
})
export class UserFormComponent implements OnInit, OnChanges {
  @Input() editUser: User | null = null;
  @Output() saved = new EventEmitter<void>();

  roles = ['ADMIN', 'OPERATOR', 'VIEWER'];
  statuses = ['ACTIVE', 'INACTIVE'];
  saving = false;
  errorMessage = '';

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: UserService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(255)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      mobile: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      role: ['OPERATOR', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editUser']) {
      const user = changes['editUser'].currentValue as User | null;
      if (user) {
        this.form.setValue({
          name: user.name,
          email: user.email,
          mobile: user.mobile,
          role: user.role,
          status: user.status
        });
      } else {
        this.form.reset({ name: '', email: '', mobile: '', role: 'OPERATOR', status: 'ACTIVE' });
      }
      this.errorMessage = '';
    }
  }

  open(): void {
    $('#userModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ name: '', email: '', mobile: '', role: 'OPERATOR', status: 'ACTIVE' });
    this.errorMessage = '';
    $('#userModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid) return;
    const request: UserRequest = {
      name: this.form.value.name.trim(),
      email: this.form.value.email.trim(),
      mobile: this.form.value.mobile.trim(),
      role: this.form.value.role,
      status: this.form.value.status
    };
    this.saving = true;
    this.errorMessage = '';

    const call = this.editUser
      ? this.service.update(this.editUser.id, request)
      : this.service.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;
        this.form.reset({ name: '', email: '', mobile: '', role: 'OPERATOR', status: 'ACTIVE' });
        $('#userModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.errorMessage = this.editUser ? 'Failed to update user.' : 'Failed to create user.';
        this.saving = false;
      }
    });
  }

  get isEditing(): boolean {
    return !!this.editUser;
  }
}
