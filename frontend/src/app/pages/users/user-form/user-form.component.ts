import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { UserService } from '../../../core/services/user.service';
import { User, UserRequest } from '../../../core/models/user.model';

declare const $: any;

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html'
})
export class UserFormComponent implements OnInit, OnChanges {
  @Input() editUser: User | null = null;
  @Output() saved = new EventEmitter<void>();

  accessLevels = [
   // { value: 1, label: 'Super Admin' },
   // { value: 2, label: 'Admin' },
    { value: 3, label: 'User' }
  ];
  saving = false;
  errorMessage = '';

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: UserService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      displayName:        ['', [Validators.required, Validators.maxLength(255)]],
      loginName:          ['', [Validators.required, Validators.maxLength(100)]],
      password:           ['', [Validators.minLength(6)]],
      userEmail:          ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      userMobileSms:      ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      userMobileWhatsapp: ['', [Validators.pattern('^[0-9]{10}$')]],
      accessLevel:        [2, Validators.required],
      enabled:            [true, Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editUser']) {
      const user = changes['editUser'].currentValue as User | null;
      if (user) {
        this.form.patchValue({
          displayName:        user.displayName,
          loginName:          user.loginName,
          password:           '',
          userEmail:          user.userEmail,
          userMobileSms:      user.userMobileSms,
          userMobileWhatsapp: user.userMobileWhatsapp,
          accessLevel:        user.accessLevel,
          enabled:            user.enabled
        });
      } else {
        this.resetForm();
      }
      this.errorMessage = '';
    }
  }

  open(): void {
    $('#userModal').modal('show');
  }

  cancel(): void {
    this.resetForm();
    this.errorMessage = '';
    $('#userModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    const request: UserRequest = {
      displayName:        v.displayName.trim(),
      loginName:          v.loginName.trim(),
      userEmail:          v.userEmail.trim(),
      userMobileSms:      v.userMobileSms.trim(),
      userMobileWhatsapp: v.userMobileWhatsapp?.trim() ?? '',
      accessLevel:        v.accessLevel,
      enabled:            v.enabled
    };
    if (!this.isEditing && v.password) {
      request.password = v.password;
    }
    this.saving = true;
    this.errorMessage = '';

    const call = this.isEditing
      ? this.service.update(this.editUser!.id, request)
      : this.service.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;
        this.resetForm();
        $('#userModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.errorMessage = this.isEditing ? 'Failed to update user.' : 'Failed to create user.';
        this.saving = false;
      }
    });
  }

  get isEditing(): boolean {
    return !!this.editUser;
  }

  private resetForm(): void {
    this.form.reset({
      displayName: '', loginName: '', password: '',
      userEmail: '', userMobileSms: '', userMobileWhatsapp: '',
      accessLevel: 2, enabled: true
    });
  }
}

