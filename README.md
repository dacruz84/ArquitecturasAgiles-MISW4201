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

1. **Clonar el repositorio:**
    ```bash
    git clone https://github.com/TU-USUARIO/ArquitecturasAgiles-Logistica-L02.git
    cd ArquitecturasAgiles-Logistica-L02

2. Crear entorno virtual:
    ```bash
    python -m venv venv
3. Activar entorno virtual:
    - Windows: 
        ```bash
        venv\Scripts\activate
    - macOS/Linux: 
        ```bash
        source venv/bin/activate
4. Instalar dependencias:
    ```bash
    pip install -r requirements.txt
5. Ejecutar el servicio:
    ```bash
    python app.py

El servicio estará disponible en: http://localhost:5000


## Pruebas con Postman

Importa este comando cURL:
    
    curl --location 'http://localhost:5000/calculate-route' \
    --header 'Content-Type: application/json' \
    --data '{"items": "P1, P3, P4, P6, P18, P5, P2, P11, P7, P10"}'

Tecnologías

- Python 3.13+
- Flask 3.1.2
- Flask-CORS 6.0.1
- Requests 2.32.5

Autor

Daniel Ricardo Ulloa Ospina - Experimento de Arquitecturas Ágiles MISO