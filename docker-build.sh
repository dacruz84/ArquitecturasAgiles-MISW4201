#!/bin/bash
# Script para construir y probar el contenedor Docker de L2

echo "🐳 CONSTRUYENDO IMAGEN DOCKER L2"
echo "================================="

# Construir la imagen
echo "Construyendo imagen logistica-l2..."
docker build -t logistica-l2 .

if [ $? -eq 0 ]; then
    echo "✅ Imagen construida exitosamente"
    
    echo ""
    echo "🚀 COMANDOS PARA EJECUTAR:"
    echo "docker run -d -p 8082:8082 --name l2-container logistica-l2"
    echo "docker logs l2-container"
    echo "docker stop l2-container"
    echo "docker rm l2-container"
    
    echo ""
    echo "🧪 PROBAR SERVICIO:"
    echo "curl http://localhost:8082/health"
    echo "curl -X POST http://localhost:8082/calculate-route -H 'Content-Type: application/json' -d '{\"items\": \"P1, P3, P4\"}'"
    
else
    echo "❌ Error construyendo la imagen"
    exit 1
fi
