# Servicio de Logística L2 - Python Flask

Microservicio de optimización de rutas en bodega implementado en Python Flask con algoritmo 3-opt.

## Descripción

Este servicio forma parte del experimento de **Voting (2-de-3)** para validar disponibilidad en microservicios de logística. Calcula rutas óptimas de
recolección de productos en bodega utilizando el algoritmo de optimización 3-opt.

## Características

- **Framework**: Python Flask
- **Algoritmo**: 3-opt para optimización de rutas
- **Tiempo de respuesta**: < 2 segundos
- **Capacidad**: Hasta 10 productos por solicitud

## Instalación

### Requisitos previos
- Python 3.7+
- pip

### Configuración

1. **Bajar el código:**
    ```bash
    # Descargar o clonar el proyecto
    cd ArquitecturasAgiles-Logistica-L02

2. Crear entorno virtual (recomendado):
    ```bash
    python -m venv venv
3. Activar entorno virtual:
    - Windows: 
        ```bash
        venv\Scripts\activate
    - Mac/Linux: 
        ```bash
        source venv/bin/activate
4. Instalar las dependencias:
    ```bash
    pip install -r requirements.txt
5. Ejecutar:
    ```bash
    python app.py

El servicio estará disponible en: http://localhost:8082

## Docker

```bash
# Construir la imagen
docker build -t logistica-l2 .

# Ejecutar el contenedor
docker run -d -p 8082:8082 --name l2-container logistica-l2

# Verificar que está corriendo
curl http://localhost:8082/up
```

## Pruebas y Validación

### Endpoints de Observabilidad

#### Health Check
```bash
curl http://localhost:8082/actuator/health
```

#### Info del Servicio
```bash
curl http://localhost:8082/actuator/info
```

#### Metricas Prometheus
```bash
curl http://localhost:8082/metrics
```

#### Health Check Simple (Docker)
```bash
curl http://localhost:8082/up
```

### Endpoint Principal (usado por Voting)
```bash
curl -X POST http://localhost:8082/logistic/route \
  -H 'Content-Type: application/json' \
  -d '{"items": "P1, P3, P4, P6, P18"}'
```

### Test Básico
```bash
python test_l2_local.py
```

## Formato de Respuesta

Según acuerdos del equipo para el experimento Voting:
```json
{
  "route": "E,P1,P3,P4,P6,P18,E"
}
```

## Tecnologías

- Python 3.11+
- Flask 3.1.2
- Flask-CORS 6.0.1
- Docker

