import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { NonAuthLayoutComponent } from './layouts/non-auth-layout/non-auth-layout.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor.class';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [AppComponent, AuthLayoutComponent, NonAuthLayoutComponent],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule],
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
