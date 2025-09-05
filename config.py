# Configuracion de L2 - Similar a application.yml de L3
import os

class Config:
    # Configuracion del servidor
    SERVER_PORT = int(os.getenv('PORT', 8082))
    SERVER_HOST = os.getenv('HOST', '0.0.0.0')
    
    # Configuracion de observabilidad (como L3)
    MANAGEMENT_ENDPOINTS = {
        'health': {'enabled': True, 'probes': True},
        'info': {'enabled': True},
        'prometheus': {'enabled': True}
    }
    
    # Tags de metricas (como L3)
    METRICS_TAGS = {
        'application': 'logistica-l2',
        'service': 'route-calculation'
    }
    
    # Configuracion del algoritmo
    RECO_MAX_PRODUCTS = int(os.getenv('RECO_MAX_PRODUCTS', 18))
    RECO_MAX_IMPROVEMENTS = int(os.getenv('RECO_MAX_IMPROVEMENTS', 1000))
