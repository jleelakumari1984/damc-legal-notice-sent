import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TemplateApprovalsComponent } from './template-approvals.component';

const routes: Routes = [{ path: '', component: TemplateApprovalsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TemplateApprovalsRoutingModule {}
