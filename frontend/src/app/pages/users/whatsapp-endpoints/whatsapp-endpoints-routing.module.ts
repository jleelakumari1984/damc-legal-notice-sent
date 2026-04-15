import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WhatsappEndpointsComponent } from './whatsapp-endpoints.component';

const routes: Routes = [{ path: '', component: WhatsappEndpointsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WhatsappEndpointsRoutingModule {}
