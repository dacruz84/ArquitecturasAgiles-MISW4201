import { Component } from '@angular/core';
import { FormService } from '../services/form.service';

@Component({
  selector: 'app-poc-form',
  templateUrl: './poc-form.component.html',
  styleUrls: ['./poc-form.component.css']
})
export class FormComponent {
  valor = '';
  status = '';
  loading = false;

  constructor(private svc: FormService) {}

  async onEnviar() {
    this.status = '';
    this.loading = true;

    try {
      const payloadObj = { valor: this.valor };
      const payloadStr = JSON.stringify(payloadObj);

      // 1) calcular SHA-256 hex del payload
      const hash = await this.sha256Hex(payloadStr);

      // 2) pedir token al backend
      const token = await this.svc.getFormToken(hash).toPromise();

      // 3) enviar payload + token al backend (endpoint /submit)
      const resp = await this.svc.submitPayload(payloadStr, token).toPromise();

      this.status = `Respuesta backend: ${resp}`;
    } catch (err: any) {
      console.error(err);
      this.status = `Error: ${err?.message || err}`;
    } finally {
      this.loading = false;
    }
  }

  // utilidad para SHA-256 y convertir a hex
  private async sha256Hex(message: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(message);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  }
}
