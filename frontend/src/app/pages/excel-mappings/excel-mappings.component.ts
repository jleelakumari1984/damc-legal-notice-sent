import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NoticeType, SendNoticesService } from '../../core/services/send-notices.service';
import { ExcelMappingRequest, ExcelMappingResponse, ExcelMappingsService } from '../../core/services/excel-mappings.service';

declare const $: any;

@Component({
  selector: 'app-excel-mappings',
  templateUrl: './excel-mappings.component.html',
  styleUrls: ['./excel-mappings.component.css']
})
export class ExcelMappingsComponent implements OnInit {
  noticeTypes: NoticeType[] = [];
  selectedProcessId: number | null = null;
  mappings: ExcelMappingResponse[] = [];

  loadingTypes = false;
  loadingMappings = false;
  saving = false;
  deleting: number | null = null;

  editingId: number | null = null;

  successMessage = '';
  errorMessage = '';

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly noticesService: SendNoticesService,
    private readonly mappingsService: ExcelMappingsService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadNoticeTypes();
  }

  private initForm(): void {
    this.form = this.fb.group({
      excelFieldName: ['', [Validators.required, Validators.maxLength(255)]],
      dbFieldName: ['', [Validators.required, Validators.maxLength(255)]],
      isKey: [0],
      isMobile: [0],
      isMandatory: [0],
      isAttachment: [0]
    });
  }

  loadNoticeTypes(): void {
    this.loadingTypes = true;
    this.noticesService.getNoticeTypes().subscribe({
      next: (data) => {
        this.noticeTypes = data;
        this.loadingTypes = false;
      },
      error: () => {
        this.loadingTypes = false;
        this.errorMessage = 'Failed to load notice types.';
      }
    });
  }

  onProcessChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.selectedProcessId = id || null;
    this.mappings = [];
    this.cancelForm();
    this.clearMessages();
    if (this.selectedProcessId) {
      this.loadMappings();
    }
  }

  loadMappings(): void {
    if (!this.selectedProcessId) return;
    this.loadingMappings = true;
    this.mappingsService.getByProcessId(this.selectedProcessId).subscribe({
      next: (data) => {
        this.mappings = data;
        this.loadingMappings = false;
      },
      error: () => {
        this.loadingMappings = false;
        this.errorMessage = 'Failed to load mappings.';
      }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ excelFieldName: '', dbFieldName: '', isKey: 0, isMobile: 0, isMandatory: 0, isAttachment: 0 });
    this.clearMessages();
    $('#mappingModal').modal('show');
  }

  openEditForm(mapping: ExcelMappingResponse): void {
    this.editingId = mapping.id;
    this.form.setValue({
      excelFieldName: mapping.excelFieldName,
      dbFieldName: mapping.dbFieldName,
      isKey: mapping.isKey ?? 0,
      isMobile: mapping.isMobile ?? 0,
      isMandatory: mapping.isMandatory ?? 0,
      isAttachment: mapping.isAttachment ?? 0
    });
    this.clearMessages();
    $('#mappingModal').modal('show');
  }

  cancelForm(): void {
    this.editingId = null;
    this.form.reset();
    $('#mappingModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid || !this.selectedProcessId) return;

    const request: ExcelMappingRequest = {
      processId: this.selectedProcessId,
      excelFieldName: this.form.value.excelFieldName.trim(),
      dbFieldName: this.form.value.dbFieldName.trim(),
      isKey: this.form.value.isKey ? 1 : 0,
      isMobile: this.form.value.isMobile ? 1 : 0,
      isMandatory: this.form.value.isMandatory ? 1 : 0,
      isAttachment: this.form.value.isAttachment ? 1 : 0
    };

    this.saving = true;
    this.clearMessages();

    const call = this.editingId
      ? this.mappingsService.update(this.editingId, request)
      : this.mappingsService.create(request);

    call.subscribe({
      next: () => {
        this.successMessage = this.editingId ? 'Mapping updated successfully.' : 'Mapping created successfully.';
        this.saving = false;
        $('#mappingModal').modal('hide');
        this.editingId = null;
        this.form.reset();
        this.loadMappings();
      },
      error: () => {
        this.errorMessage = this.editingId ? 'Failed to update mapping.' : 'Failed to create mapping.';
        this.saving = false;
      }
    });
  }

  confirmDelete(mapping: ExcelMappingResponse): void {
    if (!confirm(`Delete mapping "${mapping.excelFieldName}"?`)) return;
    this.deleting = mapping.id;
    this.clearMessages();
    this.mappingsService.delete(mapping.id).subscribe({
      next: () => {
        this.deleting = null;
        this.successMessage = 'Mapping deleted.';
        this.loadMappings();
      },
      error: () => {
        this.deleting = null;
        this.errorMessage = 'Failed to delete mapping.';
      }
    });
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  flagLabel(value: number): string {
    return value === 1 ? 'Yes' : 'No';
  }

  flagBadge(value: number): string {
    return value === 1 ? 'bg-success' : 'bg-secondary';
  }
}
