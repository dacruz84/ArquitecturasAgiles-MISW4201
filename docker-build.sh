#!/bin/bash
# Script para construir y probar el contenedor Docker de L2
# Microservicio de logistica para experimento Voting

echo "CONSTRUYENDO IMAGEN DOCKER L2 - LOGISTICA"
echo "============================================="

# Limpiar contenedores anteriores si existen
echo "Limpiando contenedores anteriores..."
docker stop l2-container 2>/dev/null || true
docker rm l2-container 2>/dev/null || true

# Construir la imagen
echo "Construyendo imagen local/service-python:latest..."
docker build -t local/service-python:latest .

if [ $? -eq 0 ]; then
    echo "Imagen construida exitosamente"
    
    echo ""
    echo "COMANDOS PARA EJECUTAR EL CONTENEDOR:"
    echo "docker run -d -p 8082:8082 --name l2-container local/service-python:latest"
    echo "docker logs -f l2-container"
    echo "docker stop l2-container && docker rm l2-container"
    
    echo ""
    echo "COMANDOS PARA PROBAR EL SERVICIO:"
    echo "# Health check simple (Docker)"
    echo "curl http://localhost:8082/up"
    echo ""
    echo "# Health check completo"
    echo "curl http://localhost:8082/actuator/health"
    echo ""
    echo "# Informacion del servicio"
    echo "curl http://localhost:8082/actuator/info"
    echo ""
    echo "# Endpoint principal (usado por Voting)"
    echo "curl -X POST http://localhost:8082/logistic/route -H 'Content-Type: application/json' -d '{\"items\": \"P1, P3, P4\"}'"
    echo ""
    echo "# Metricas de Prometheus"
    echo "curl http://localhost:8082/metrics"
    echo ""
    echo "============================================="
    
else
    echo "Error construyendo la imagen"
    exit 1
fi
