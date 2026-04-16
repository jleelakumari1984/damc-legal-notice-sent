import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { UserSmsCredentialService } from '../../../../core/services/user-sms-credential.service';
import { UserSmsCredential } from '../../../../core/models/endpoint.model';

@Component({
  selector: 'app-sms-endpoints',
  templateUrl: './sms-endpoints.component.html'
})
export class SmsEndpointsComponent implements OnInit {
  @Input() userId: number | null = null;

  credential: UserSmsCredential | null = null;
  form!: FormGroup;
  loading = false;
  saving = false;
  loadError = '';
  saveError = '';
  saveSuccess = false;

  constructor(private readonly fb: FormBuilder, private readonly service: UserSmsCredentialService) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      url: ['', Validators.required],
      userName: ['', Validators.required],
      password: [''],
      live: [false],
      testMobileNumber: ['']
    });

    if (this.userId !== null) {
      this.load();
    }
  }

  private load(): void {
    this.loading = true;
    this.loadError = '';
    this.service.get(this.userId!).subscribe({
      next: (cred) => {
        this.credential = cred;
        if (cred) {
          this.form.patchValue({
            url: cred.url,
            userName: cred.userName,
            password: '',
            live: cred.live,
            testMobileNumber: cred.testMobileNumber ?? ''
          });
        }

        this.loading = false;
      },
      error: (err) => {
        // 404 = no credential yet, treat as empty form
        if (err?.status !== 404) {
          this.loadError = 'Failed to load SMS credential.';
        }
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    const request: any = {
      url: v.url.trim(),
      userName: v.userName.trim(),
      live: v.live,
      testMobileNumber: v.testMobileNumber?.trim() || null
    };
    if (v.password?.trim()) {
      request.password = v.password.trim();
    }
    this.saving = true;
    this.saveError = '';
    this.saveSuccess = false;
    this.service.save(this.userId!, request).subscribe({
      next: (cred) => {
        this.credential = cred;
        this.saving = false;
        this.saveSuccess = true;
        this.form.patchValue({ password: '' });
      },
      error: () => {
        this.saving = false;
        this.saveError = 'Failed to save SMS credential.';
      }
    });
  }
}
