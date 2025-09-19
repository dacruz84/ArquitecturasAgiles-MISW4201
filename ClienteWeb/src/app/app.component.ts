import { Component } from '@angular/core';

import { PocFormComponent } from './poc-form.component';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [PocFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'poc-tamper-ui';
}
