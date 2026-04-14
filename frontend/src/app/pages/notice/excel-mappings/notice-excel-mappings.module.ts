import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { NoticeExcelMappingsRoutingModule } from './notice-excel-mappings-routing.module';
import { NoticeExcelMappingsComponent } from './notice-excel-mappings.component';
import { NoticeExcelMappingFormComponent } from './notice-excel-mapping-form/notice-excel-mapping-form.component';
import { ExcelMappingsListComponent } from './list/excel-mappings-list.component';

@NgModule({
  declarations: [NoticeExcelMappingsComponent, NoticeExcelMappingFormComponent, ExcelMappingsListComponent],
  exports: [NoticeExcelMappingsComponent, ExcelMappingsListComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule,  NoticeExcelMappingsRoutingModule]
})
export class NoticeExcelMappingsModule {}
