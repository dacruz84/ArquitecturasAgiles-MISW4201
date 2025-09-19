# ClienteWeb - Angular PoC

Este cliente web es una prueba de concepto para experimentos de seguridad. Contiene un formulario con un campo string prellenado y un botón de enviar. La petición enviará el contenido al backend incluyendo un token de seguridad (a implementar).

## Desarrollo local

```sh
npm install
npm start
```

## Build y Docker

```sh
docker build -t clienteweb .
```

## Uso en Docker Compose

Agrega un servicio en tu `compose.yaml` similar a:

```yaml
  clienteweb:
    build: ./ClienteWeb
    ports:
      - "4200:80"
```

