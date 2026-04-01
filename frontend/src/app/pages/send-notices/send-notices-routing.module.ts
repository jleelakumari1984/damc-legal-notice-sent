import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SendNoticesComponent } from './send-notices.component';

const routes: Routes = [{ path: '', component: SendNoticesComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SendNoticesRoutingModule {}