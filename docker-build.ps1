# Script PowerShell para construir y probar el contenedor Docker de L2

Write-Host "🐳 CONSTRUYENDO IMAGEN DOCKER L2" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

# Construir la imagen
Write-Host "Construyendo imagen logistica-l2..." -ForegroundColor Yellow
docker build -t logistica-l2 .

if ($LASTEXITCODE -eq 0) {
    Write-Host "Imagen construida exitosamente" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "COMANDOS PARA EJECUTAR:" -ForegroundColor Cyan
    Write-Host "docker run -d -p 8082:8082 --name l2-container logistica-l2" -ForegroundColor White
    Write-Host "docker logs l2-container" -ForegroundColor White
    Write-Host "docker stop l2-container" -ForegroundColor White
    Write-Host "docker rm l2-container" -ForegroundColor White
    
    Write-Host ""
    Write-Host "PROBAR SERVICIO:" -ForegroundColor Cyan
    Write-Host "curl http://localhost:8082/health" -ForegroundColor White
    Write-Host "curl -X POST http://localhost:8082/calculate-route -H 'Content-Type: application/json' -d '{`"items`": `"P1, P3, P4`"}'" -ForegroundColor White
    
} else {
    Write-Host "Error construyendo la imagen" -ForegroundColor Red
    exit 1
}
