import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeExcelMappingsService } from '../../../../core/services/notice-excel-mappings.service';
import { NoticeExcelMappingRequest, NoticeExcelMappingResponse, NoticeType } from '../../../../core/models/notices.model';

declare const $: any;

@Component({
  selector: 'app-notice-excel-mapping-form',
  templateUrl: './notice-excel-mapping-form.component.html'
})
export class NoticeExcelMappingFormComponent implements OnInit, OnChanges {
  @Input() editMapping: NoticeExcelMappingResponse | null = null;
  @Input() noticeType: NoticeType | null = null;
  @Output() saved = new EventEmitter<void>();

  saving = false;
  errorMessage = '';

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: NoticeExcelMappingsService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      excelFieldName: ['', [Validators.required, Validators.maxLength(255)]],
      dbFieldName: ['', [Validators.required, Validators.maxLength(255)]],
      isKey: [0],
      isMobile: [0],
      isMandatory: [0],
      isAttachment: [0]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editMapping']) {
      const m = changes['editMapping'].currentValue as NoticeExcelMappingResponse | null;
      if (m) {
        this.form.setValue({
          excelFieldName: m.excelFieldName,
          dbFieldName: m.dbFieldName,
          isKey: m.isKey ?? 0,
          isMobile: m.isMobile ?? 0,
          isMandatory: m.isMandatory ?? 0,
          isAttachment: m.isAttachment ?? 0
        });
      } else {
        this.form.reset({ excelFieldName: '', dbFieldName: '', isKey: 0, isMobile: 0, isMandatory: 0, isAttachment: 0 });
      }
      this.errorMessage = '';
    }
  }

  open(): void {
    $('#mappingModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ excelFieldName: '', dbFieldName: '', isKey: 0, isMobile: 0, isMandatory: 0, isAttachment: 0 });
    this.errorMessage = '';
    $('#mappingModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.noticeType) return;
    const request: NoticeExcelMappingRequest = {
      noticeId: this.noticeType.id,
      excelFieldName: this.form.value.excelFieldName.trim(),
      dbFieldName: this.form.value.dbFieldName.trim(),
      isKey: this.form.value.isKey ? 1 : 0,
      isMobile: this.form.value.isMobile ? 1 : 0,
      isMandatory: this.form.value.isMandatory ? 1 : 0,
      isAttachment: this.form.value.isAttachment ? 1 : 0
    };
    this.saving = true;
    this.errorMessage = '';

    const call = this.editMapping
      ? this.service.update(this.editMapping.id, request)
      : this.service.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;
        this.form.reset({ excelFieldName: '', dbFieldName: '', isKey: 0, isMobile: 0, isMandatory: 0, isAttachment: 0 });
        $('#mappingModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.errorMessage = this.editMapping ? 'Failed to update mapping.' : 'Failed to create mapping.';
        this.saving = false;
      }
    });
  }

  get isEditing(): boolean {
    return !!this.editMapping;
  }
}
