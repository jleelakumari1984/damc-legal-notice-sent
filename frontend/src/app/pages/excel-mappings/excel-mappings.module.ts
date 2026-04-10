import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ExcelMappingsRoutingModule } from './excel-mappings-routing.module';
import { ExcelMappingsComponent } from './excel-mappings.component';

@NgModule({
  declarations: [ExcelMappingsComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ExcelMappingsRoutingModule]
})
export class ExcelMappingsModule {}
