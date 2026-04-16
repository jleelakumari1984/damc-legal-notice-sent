import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

import { NoticeService } from '../../../../core/services/notice.service';
import { NoticeRequest, NoticeType } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-notice-form',
  templateUrl: './notice-form.component.html'
})
export class NoticeFormComponent implements OnInit, OnChanges, OnDestroy {
  @Input() editNotice: NoticeType | null = null;
  @Output() saved = new EventEmitter<void>();

  saving = false;
  errorMessage = '';

  form!: FormGroup;

  private typeSub!: Subscription;

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: NoticeService,
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editNotice']) {
      const notice = changes['editNotice'].currentValue as NoticeType | null;
      if (notice) {
        this.form.setValue({
          title: notice.name,
          description: notice.description
        });
      } else {
        this.form.reset({ title: '', description: '' });
      }
      this.errorMessage = '';
    }
  }

  ngOnDestroy(): void {
    this.typeSub?.unsubscribe();
  }

  open(): void {
    $('#noticeModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ title: '', description: '' });
    this.errorMessage = '';
    $('#noticeModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid) return;
    const request: NoticeRequest = {
      name: this.form.value.title.trim(),
      description: this.form.value.description.trim()
    };
    this.saving = true;
    this.errorMessage = '';

    const call = this.editNotice
      ? this.service.update(this.editNotice.id, request)
      : this.service.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;
        this.form.reset({ title: '' });
        $('#noticeModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.errorMessage = this.editNotice ? 'Failed to update notice.' : 'Failed to create notice.';
        this.saving = false;
      }
    });
  }

  get isEditing(): boolean {
    return !!this.editNotice;
  }
}
