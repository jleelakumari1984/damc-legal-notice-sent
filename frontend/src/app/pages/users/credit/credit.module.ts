import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { CreditRoutingModule } from './credit-routing.module';
import { CreditComponent } from './credit.component';
import { CreditFormComponent } from './credit-form/credit-form.component';

@NgModule({
  declarations: [CreditComponent, CreditFormComponent],
  exports: [CreditComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, CreditRoutingModule]
})
export class CreditModule {}
