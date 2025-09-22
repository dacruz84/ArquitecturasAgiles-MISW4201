import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { KeycloakService } from './keycloak.service';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { environment } from '../environments/environment';

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

  sending = false;
  sendResult: any = null;
  sendError: string | null = null;

  constructor(private fb: FormBuilder, private kc: KeycloakService, private http: HttpClient) {
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

  async sendToGateway(): Promise<void> {
    if (!this.hashResult) {
      alert('Primero genera el hash.');
      return;
    }
    if (!this.isLogistica()) {
      alert('No tienes permiso (requiere rol LOGISTICA).');
      return;
    }
    const token = this.kc.getToken();
    if (!token) {
      alert('Token no disponible – quizá la sesión expiró.');
      return;
    }

    this.sending = true;
    this.sendResult = null;
    this.sendError = null;
    const body = {
      payload: this.form.value.payload,
      hash: this.hashResult
    };
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    try {
      const resp = await this.http.post(`${environment.gateway.baseUrl}/voting`, body, { headers }).toPromise();
      this.sendResult = resp;
    } catch (err) {
      if (err instanceof HttpErrorResponse) {
        this.sendError = `Error ${err.status}: ${err.message}`;
      } else {
        this.sendError = 'Error desconocido enviando al Gateway';
      }
    } finally {
      this.sending = false;
    }
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