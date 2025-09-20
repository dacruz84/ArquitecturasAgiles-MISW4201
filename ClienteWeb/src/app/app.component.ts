import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { KeycloakService } from './keycloak.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'clienteweb';

  form: FormGroup;
  hashResult: string | null = null;
  isHashing = false;

  constructor(private fb: FormBuilder, private kc: KeycloakService) {
    this.form = this.fb.group({
      payload: [
        'P1,P3,P4,P6,P18,P5,P2,P11,P7,P10',
        [Validators.required, Validators.minLength(3)]
      ]
    });
  }

  async generateHash(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.isHashing = true;
    this.hashResult = null;
    try {
      const payload = this.form.value.payload as string;
      this.hashResult = await this.sha256Hex(payload);
    } catch (e) {
      console.error('Error generando hash', e);
      this.hashResult = 'ERROR';
    } finally {
      this.isHashing = false;
    }
  }

  // Utiliza Web Crypto API
  private async sha256Hex(text: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(text);
    const digest = await crypto.subtle.digest('SHA-256', data);
    const bytes = Array.from(new Uint8Array(digest));
    return bytes.map(b => b.toString(16).padStart(2, '0')).join('');
  }

  // Placeholder: luego usaremos esto para enviar al Gateway con token y hash
  sendToGateway(): void {
    if (!this.hashResult) {
      alert('Primero genera el hash.');
      return;
    }
    // Aquí luego: llamar servicio Auth -> obtener token -> POST con payload + hash
    console.log('Preparado para enviar:', {
      payload: this.form.value.payload,
      hash: this.hashResult
    });
  }

  
  get username(): string | null {
    return this.kc.getUsername();
  }

  isCompras(): boolean {
    return this.kc.hasRole('COMPRAS');
  }

  isLogistica(): boolean {
    return this.kc.hasRole('LOGISTICA');
  }

  logout(): void {
    this.kc.logout();
  }
}