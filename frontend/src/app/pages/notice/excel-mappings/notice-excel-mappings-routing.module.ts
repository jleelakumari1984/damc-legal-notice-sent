import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NoticeExcelMappingsComponent } from './notice-excel-mappings.component';

const routes: Routes = [{ path: '', component: NoticeExcelMappingsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NoticeExcelMappingsRoutingModule {}
