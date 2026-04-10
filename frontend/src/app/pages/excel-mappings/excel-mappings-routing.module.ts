import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ExcelMappingsComponent } from './excel-mappings.component';

const routes: Routes = [{ path: '', component: ExcelMappingsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ExcelMappingsRoutingModule {}
