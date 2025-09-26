# route-picking-3opt-mid

Spring Boot (Java 17) — estructura intermedia (simple pero ordenada):
- Matriz de distancias en arrays estáticos (`MatrixStatic`)
- Algoritmo 3-opt (`Tsp3OptService`)
- Servicio de planificación (`RoutePlannerService`)
- Controller y DTOs simples
- Límite 10 productos y falla aleatoria 10%

## Ejecutar
mvn -q -DskipTests package
java -jar target/route-picking-3opt-mid-0.0.1-SNAPSHOT.jar

## Probar
curl -s -X POST http://localhost:8081/api/v1/routes/optimal-order   -H 'Content-Type: application/json'   -d '{"points":["P4","P17","P9","P2","P12"]}' | jq


