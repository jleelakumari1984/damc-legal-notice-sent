import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { UsersRoutingModule } from './users-routing.module';
import { UsersComponent } from './users.component';
import { UserFormComponent } from './user-form/user-form.component';
import { CreditModule } from './credit/credit.module';

@NgModule({
  declarations: [UsersComponent, UserFormComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, UsersRoutingModule, CreditModule]
})
export class UsersModule {}
