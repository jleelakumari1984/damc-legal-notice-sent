import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SmsEndpointsComponent } from './sms-endpoints.component';

const routes: Routes = [{ path: '', component: SmsEndpointsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SmsEndpointsRoutingModule {}
