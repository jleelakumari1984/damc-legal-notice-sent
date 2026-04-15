import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NoticeExcelMappingFormComponent } from './notice-excel-mapping-form/notice-excel-mapping-form.component';
import { NoticeExcelMappingResponse, NoticeType } from '../../../core/models/notices.model';
import { NoticeService } from '../../../core/services/notice.service';
import { NoticeReportRequest } from '../../../core/models/report.notice';

@Component({
  selector: 'app-notice-excel-mappings',
  templateUrl: './notice-excel-mappings.component.html',
  styleUrls: ['./notice-excel-mappings.component.css']
})
export class NoticeExcelMappingsComponent implements OnInit, AfterViewInit {
  @ViewChild(NoticeExcelMappingFormComponent) mappingForm!: NoticeExcelMappingFormComponent;
  @Input() selectedNotice: NoticeType | null = null;

  @Input() selectFrom: String = "default";
  @Output() onClose = new EventEmitter<void>();
  noticeTypes: NoticeType[] = [];
  editMapping: NoticeExcelMappingResponse | null = null;
  refreshTrigger = 0;

  loadingTypes = false;

  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly noticesService: NoticeService,
    private readonly route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    if (this.selectFrom === "default") {
      this.loadNoticeTypes();
    }
    this.route.queryParams.subscribe(params => {
      const processId = Number(params['processId']);
    });
  }

  ngAfterViewInit(): void { }

  loadNoticeTypes(): void {
    this.loadingTypes = true;
    this.noticesService.getNoticeTypes().subscribe({
      next: (data) => {
        this.noticeTypes = data.data;
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
    this.selectedNotice = this.noticeTypes.find(nt => nt.id === id) || null;
    this.editMapping = null;
    this.clearMessages();
  }

  openAddForm(): void {
    this.editMapping = null;
    this.clearMessages();
    this.mappingForm.open();
  }

  openEditForm(mapping: NoticeExcelMappingResponse): void {
    this.editMapping = mapping;
    this.clearMessages();
    this.mappingForm.open();
  }

  onSaved(): void {
    this.successMessage = this.editMapping ? 'Mapping updated successfully.' : 'Mapping created successfully.';
    this.editMapping = null;
    this.refreshTrigger++;
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}

