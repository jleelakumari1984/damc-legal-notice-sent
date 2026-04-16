import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { UserWhatsAppCredentialService } from '../../../../core/services/user-whatsapp-credential.service';
import { UserWhatsAppCredential } from '../../../../core/models/endpoint.model';

@Component({
  selector: 'app-whatsapp-endpoints',
  templateUrl: './whatsapp-endpoints.component.html'
})
export class WhatsappEndpointsComponent implements OnInit {
  @Input() userId: number | null = null;

  credential: UserWhatsAppCredential | null = null;
  form!: FormGroup;
  loading = false;
  saving = false;
  loadError = '';
  saveError = '';
  saveSuccess = false;

  constructor(private readonly fb: FormBuilder, private readonly service: UserWhatsAppCredentialService) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      url: ['', Validators.required],
      accessToken: [''],
      attachmentDownloadUrl: [''],
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
            accessToken: '',
            attachmentDownloadUrl: cred.attachmentDownloadUrl ?? '',
            live: cred.live,
            testMobileNumber: cred.testMobileNumber ?? ''
          });
        }
        this.loading = false;
      },
      error: (err) => {
        if (err?.status !== 404) {
          this.loadError = 'Failed to load WhatsApp credential.';
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
      live: v.live,
      attachmentDownloadUrl: v.attachmentDownloadUrl?.trim() || null,
      testMobileNumber: v.testMobileNumber?.trim() || null
    };
    if (v.accessToken?.trim()) {
      request.accessToken = v.accessToken.trim();
    }
    this.saving = true;
    this.saveError = '';
    this.saveSuccess = false;
    this.service.save(this.userId!, request).subscribe({
      next: (cred) => {
        this.credential = cred;
        this.saving = false;
        this.saveSuccess = true;
        this.form.patchValue({ accessToken: '' });
      },
      error: () => {
        this.saving = false;
        this.saveError = 'Failed to save WhatsApp credential.';
      }
    });
  }
}
