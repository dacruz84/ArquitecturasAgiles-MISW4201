
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-poc-form',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <label for="payload">Payload:</label>
      <input id="payload" formControlName="payload" style="width: 100%; margin-bottom: 1rem;" />
      <button type="submit">Enviar</button>
    </form>
  `,
})
export class PocFormComponent {
  form: FormGroup;


  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      payload: [
        'P1, P3, P4, P6, P18, P5, P2, P11, P7, P10',
      ],
    });
  }

  onSubmit() {
    // 1. Pedir token temporal (simulado)
    this.http.get<{token: string}>('https://dummyjson.com/auth/token')
      .subscribe({
        next: (resp) => {
          const token = resp.token || 'token-temporal';
          // 2. Enviar el payload al backend con el token en el header
          this.http.post('https://jsonplaceholder.typicode.com/posts',
            { payload: this.form.value.payload },
            { headers: { Authorization: `Bearer ${token}` } }
          ).subscribe({
            next: (result) => {
              alert('Payload enviado correctamente. Respuesta: ' + JSON.stringify(result));
            },
            error: () => {
              alert('Error al enviar el payload al backend.');
            }
          });
        },
        error: () => {
          alert('No se pudo obtener el token.');
        }
      });
  }
}
