export const environment = {
  production: false,
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'arquitectura',
    clientId: 'clienteweb'
  },
  gateway: {
    // Para el navegador (desarrollo) usamos localhost; dentro de Docker la app Nginx usaría hostname "gateway".
    baseUrl: 'http://localhost:3009'
  }
};