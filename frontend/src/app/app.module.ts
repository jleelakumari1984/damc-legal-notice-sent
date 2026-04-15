import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { NonAuthLayoutComponent } from './layouts/non-auth-layout/non-auth-layout.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor.class';
import { AppComponent } from './app.component';
import { SwitchSessionComponent } from './shared/switch-session/switch-session.component';
import { ConfirmModalComponent } from './shared/confirm-modal/confirm-modal.component';

@NgModule({
  declarations: [AppComponent, AuthLayoutComponent, NonAuthLayoutComponent, SwitchSessionComponent, ConfirmModalComponent],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
