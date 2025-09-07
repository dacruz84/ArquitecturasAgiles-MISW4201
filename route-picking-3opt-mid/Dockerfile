# ---- Stage 1: Build (Maven + JDK 17) ----
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q clean package spring-boot:repackage -DskipTests

# ---- Stage 2: Runtime (JRE 17) ----
FROM eclipse-temurin:17-jre

# Usuario no-root por seguridad
RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /app

COPY --from=builder /workspace/target/route-picking-3opt-mid-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8081

# Variables opcionales para identificar para configurar fallo simulado

ENV RECO_FAILURE_RATE=0.10
ENV RECO_MAX_PRODUCTS=10
ENV RECO_MAX_SWEEPS=1000
ENV RECO_MAX_IMPROVEMENTS=5000
ENV PORT=8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
