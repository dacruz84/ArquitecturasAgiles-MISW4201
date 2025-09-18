# Dockerfile para Logistica L2 - Python Flask
# Servicio de optimización de rutas para experimento Voting

# Usar imagen oficial de Python 3.11 (estable y no muy nueva)
FROM python:3.11-slim

# Información del mantenedor
LABEL maintainer="Daniel Ulloa - Experimento Arquitecturas Agiles"
LABEL description="Microservicio L2 - Optimización de rutas con 3-opt"

# Instalar curl para health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN useradd -ms /bin/bash appuser

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de dependencias primero (para cache de Docker)
COPY requirements.txt .

# Instalar dependencias de Python
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el código de la aplicación
COPY app.py .
COPY config.py .

# Cambiar al usuario no-root
USER appuser

# Puerto que expone el servicio (8082 para producción, como está en app.py)
EXPOSE 8082

# Variables de entorno para el experimento
ENV FLASK_ENV=production
ENV PYTHONUNBUFFERED=1

# Health check para verificar que el servicio esté funcionando
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8082/up || exit 1

# Comando para ejecutar la aplicación
CMD ["python", "app.py"]