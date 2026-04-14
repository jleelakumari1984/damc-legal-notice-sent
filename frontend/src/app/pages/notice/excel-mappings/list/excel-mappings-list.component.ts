import { AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

import { DatatableHelper } from '../../../../shared/datatable/datatable.helper';
import { ExcelMappingsDatatable } from '../../../../shared/datatable/excel-mappings-datatable';
import { NoticeExcelMappingsService } from '../../../../core/services/notice-excel-mappings.service';
import { NoticeExcelMappingResponse, NoticeType } from '../../../../core/models/notices.model';

@Component({
  selector: 'app-excel-mappings-list',
  templateUrl: './excel-mappings-list.component.html'
})
export class ExcelMappingsListComponent implements AfterViewInit, OnChanges {
  editMapping: NoticeExcelMappingResponse | null = null;

  @Input() selectedNotice: NoticeType | null = null;
  @Input() refreshTrigger = 0;

  @Output() editRequested = new EventEmitter<NoticeExcelMappingResponse>();

  successMessage = '';
  errorMessage = '';
  deleting: number | null = null;

  private tableInitialized = false;

  constructor(
    private readonly mappingsService: NoticeExcelMappingsService,
    private readonly datatableHelper: DatatableHelper,
    private readonly excelMapService: NoticeExcelMappingsService
  ) { }

  ngAfterViewInit(): void {
    if (this.selectedNotice) {
      this.loadMappings();
      this.tableInitialized = true;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedNotice'] && !changes['selectedNotice'].firstChange && this.selectedNotice) {
      setTimeout(() => this.loadMappings(), 0);
    }
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.datatableHelper.reload('#mappingsTable');
    }
  }

  loadMappings(): void {
    if (!this.selectedNotice) return;
    this.datatableHelper.initTable('#mappingsTable', new ExcelMappingsDatatable(
      this.selectedNotice.id,
      {
        onEdit: (mapping) => {
          this.editMapping = mapping;
          this.editRequested.emit(mapping);
        },
        onDelete: (mapping) => this.confirmDelete(mapping),
        onError: (message) => this.errorMessage = message
      },
      this.excelMapService
    ));
    this.tableInitialized = true;
  }

  confirmDelete(mapping: NoticeExcelMappingResponse): void {
    if (!confirm(`Delete mapping "${mapping.excelFieldName}"?`)) return;
    this.deleting = mapping.id;
    this.errorMessage = '';
    this.mappingsService.delete(mapping.id).subscribe({
      next: () => {
        this.deleting = null;
        this.successMessage = 'Mapping deleted.';
        this.datatableHelper.reload('#mappingsTable');
      },
      error: () => {
        this.deleting = null;
        this.errorMessage = 'Failed to delete mapping.';
      }
    });
  }


  onSaved(): void {
    this.successMessage = this.editMapping ? 'Mapping updated successfully.' : 'Mapping created successfully.';
    this.editMapping = null;
    this.refreshTrigger++;
  }
}
