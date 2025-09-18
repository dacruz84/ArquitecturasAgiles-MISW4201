import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PocFormComponent } from './poc-form/poc-form.component';
import { FormComponentComponent } from './FormComponent/FormComponent.component';

@NgModule({
  declarations: [	
    AppComponent,
    PocFormComponent,
      FormComponentComponent
   ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
