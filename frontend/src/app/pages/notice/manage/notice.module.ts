import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NoticeRoutingModule } from './notice-routing.module';
import { NoticeComponent } from './notice.component';
import { NoticeFormComponent } from './notice-form/notice-form.component';
import { NoticeExcelMappingsModule } from '../excel-mappings/notice-excel-mappings.module';

@NgModule({
  declarations: [NoticeComponent, NoticeFormComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NoticeRoutingModule, NoticeExcelMappingsModule]
})
export class NoticeModule { }
