import { Injectable } from '@angular/core';
import Keycloak, { KeycloakInstance } from 'keycloak-js';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class KeycloakService {

  private kc?: KeycloakInstance;

  init(): Promise<boolean> {
    this.kc = new Keycloak({
      url: environment.keycloak.url,
      realm: environment.keycloak.realm,
      clientId: environment.keycloak.clientId
    });

    return this.kc.init({
      onLoad: 'login-required',
      pkceMethod: 'S256'
    }).then(auth => {
      if (auth) {
        this.scheduleRefresh();
      }
      return auth;
    }).catch(err => {
      console.error('Keycloak init error', err);
      return false;
    });
  }

  private scheduleRefresh() {
    if (!this.kc) return;
    // Refresco simple cada 30s
    setInterval(() => {
      if (!this.kc) return;
      this.kc.updateToken(60).catch(e => console.warn('No se pudo refrescar token', e));
    }, 30000);
  }

  getToken(): string | null {
    return this.kc?.token || null;
  }

  getUsername(): string | null {
    return this.kc?.tokenParsed?.['preferred_username'] || null;
  }

  hasRole(role: string): boolean {
    const roles: string[] = this.kc?.tokenParsed?.['realm_access']?.['roles'] || [];
    return roles.includes(role);
  }

  login() {
    this.kc?.login();
  }

  logout() {
    this.kc?.logout({ redirectUri: window.location.origin });
  }

  account() {
    this.kc?.accountManagement();
  }
}