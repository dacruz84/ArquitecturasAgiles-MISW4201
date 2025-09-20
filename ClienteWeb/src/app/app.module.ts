import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { APP_INITIALIZER } from '@angular/core';
import { KeycloakService } from './keycloak.service';

// NUEVO
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

export function kcFactory(kc: KeycloakService) {
  return () => kc.init();
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,   // NUEVO
    HttpClientModule       // NUEVO
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: kcFactory,
      deps: [KeycloakService],
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }