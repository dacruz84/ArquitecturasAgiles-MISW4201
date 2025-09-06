from flask import Flask, request, jsonify
from flask_cors import CORS
import time
from datetime import datetime
from config import Config

# Observabilidad estándar (como L3)
from prometheus_flask_exporter import PrometheusMetrics

app = Flask(__name__)
CORS(app)

# Cargar configuracion manualmente
config = Config()
app.config['SERVER_PORT'] = config.SERVER_PORT
app.config['SERVER_HOST'] = config.SERVER_HOST
#app.config['RECO_MAX_PRODUCTS'] = config.RECO_MAX_PRODUCTS

# Configurar metricas automaticas como L3
metrics = PrometheusMetrics(app)
metrics.info('logistica_l2_info', 'Informacion de L2', 
             version='1.0.0', 
             technology='Python-Flask',
             algorithm='3-opt')

# Tiempo de inicio del servicio
inicio_servicio = datetime.now()

# Configuracion de la bodega - Matriz de distancias
# E = entrada, P1 a P18 = productos en la bodega
NODOS_BODEGA = ["E","P1","P2","P3","P4","P5","P6","P7","P8","P9","P10","P11","P12","P13","P14","P15","P16","P17","P18"]

# Matriz de distancias
MATRIZ_DISTANCIAS = [
    [0,1,2,3,4,3,2,3,4,5,6,5,4,5,6,7,8,7,6],   # E
    [1,0,1,2,3,2,1,2,3,4,5,4,3,4,5,6,7,6,5],   # P1
    [2,1,0,1,2,1,2,3,2,3,4,3,4,5,6,5,6,7,6],   # P2
    [3,2,1,0,1,2,3,4,3,2,3,4,5,6,5,6,7,6,5],   # P3
    [4,3,2,1,0,1,2,3,4,3,2,3,4,5,4,5,6,5,4],   # P4
    [3,2,1,2,1,0,1,2,3,2,3,4,3,4,5,6,5,6,5],   # P5
    [2,1,2,3,2,1,0,1,2,3,4,3,3,4,5,6,6,5,4],   # P6
    [3,2,3,4,3,2,1,0,1,2,3,2,1,2,3,4,5,4,3],   # P7
    [4,3,2,3,4,3,2,1,0,1,2,3,2,3,2,3,4,3,2],   # P8
    [5,4,3,2,3,2,3,2,1,0,1,2,3,4,3,2,3,4,3],   # P9
    [6,5,4,3,2,3,4,3,2,1,0,1,2,3,2,1,2,3,4],   # P10
    [5,4,3,4,3,4,3,2,3,2,1,0,1,2,3,2,1,2,3],   # P11
    [4,3,4,5,4,3,3,1,2,3,2,1,0,1,2,3,2,1,2],   # P12
    [5,4,5,6,5,4,4,2,3,4,3,2,1,0,1,2,3,2,1],   # P13
    [6,5,6,5,4,5,5,3,2,3,2,3,2,1,0,1,2,3,2],   # P14
    [7,6,5,6,5,6,6,4,3,2,1,2,3,2,1,0,1,2,1],   # P15
    [8,7,6,7,6,5,6,5,4,3,2,1,2,3,2,1,0,1,2],   # P16
    [7,6,7,6,5,6,5,4,3,4,3,2,1,2,3,2,1,0,1],   # P17
    [6,5,6,5,4,5,4,3,2,3,4,3,2,1,2,1,2,1,0]    # P18
]

# Diccionario para encontrar el índice de cada nodo en la matriz
INDICE_NODOS = {}
for i, nodo in enumerate(NODOS_BODEGA):
    INDICE_NODOS[nodo] = i

def calcular_distancia(producto1, producto2):
    #Calcula la distancia entre dos productos usando la matriz
    # Verificar que los productos existan
    if producto1 not in INDICE_NODOS:
        return 999.0  # distancia muy grande si no existe
    if producto2 not in INDICE_NODOS:
        return 999.0

    idx1 = INDICE_NODOS[producto1]
    idx2 = INDICE_NODOS[producto2]

    # Buscar en la matriz
    distancia = MATRIZ_DISTANCIAS[idx1][idx2]
    return distancia

def calcular_distancia_ruta(ruta):
    #Suma todas las distancias de la ruta completa
    distancia_total = 0.0
    # Sumar distancia entre cada par de nodos consecutivos
    for i in range(len(ruta) - 1):
        nodo_actual = ruta[i]
        nodo_siguiente = ruta[i + 1]
        distancia_total += calcular_distancia(nodo_actual, nodo_siguiente)
    return distancia_total

def invertir_segmento(ruta, inicio, fin):
    #Invierte una parte de la ruta - para el 3-opt
    nueva_ruta = ruta.copy()
    nueva_ruta[inicio:fin+1] = reversed(nueva_ruta[inicio:fin+1])
    return nueva_ruta

def mejorar_con_3opt(ruta):

    #Algoritmo 3-opt para optimizar la ruta
    mejor_ruta = ruta.copy()
    mejor_distancia = calcular_distancia_ruta(mejor_ruta)

    n = len(ruta)
    max_mejoras = 100  # límite para que no se cuelgue el algoritmo
    mejoras = 0
    
    sigue_mejorando = True
    while sigue_mejorando and mejoras < max_mejoras:
        sigue_mejorando = False

        # Probar todas las combinaciones de 3 segmentos
        for i in range(1, n - 3):
            for j in range(i + 1, n - 2):
                for k in range(j + 1, n - 1):

                    # Caso 1: Invertir primer segmento
                    nueva_ruta = invertir_segmento(ruta, i, j-1)
                    nueva_distancia = calcular_distancia_ruta(nueva_ruta)

                    if nueva_distancia < mejor_distancia:
                        mejor_ruta = nueva_ruta
                        mejor_distancia = nueva_distancia
                        sigue_mejorando = True
                        mejoras += 1
                        break

                    # Caso 2: Invertir segundo segmento
                    nueva_ruta = invertir_segmento(ruta, j, k-1)
                    nueva_distancia = calcular_distancia_ruta(nueva_ruta)

                    if nueva_distancia < mejor_distancia:
                        mejor_ruta = nueva_ruta
                        mejor_distancia = nueva_distancia
                        sigue_mejorando = True
                        mejoras += 1
                        break

                    # Caso 3: Invertir ambos segmentos
                    nueva_ruta = invertir_segmento(ruta, i, j-1)
                    nueva_ruta = invertir_segmento(nueva_ruta, j, k-1)
                    nueva_distancia = calcular_distancia_ruta(nueva_ruta)

                    if nueva_distancia < mejor_distancia:
                        mejor_ruta = nueva_ruta
                        mejor_distancia = nueva_distancia
                        sigue_mejorando = True
                        mejoras += 1
                        break

                if sigue_mejorando:
                    break
            if sigue_mejorando:
                break

        ruta = mejor_ruta.copy()

    #cuántas mejoras hizo
    if mejoras > 0:
        print(f"3-opt mejoró {mejoras} veces, distancia final: {mejor_distancia}")
    return mejor_ruta

def optimizar_ruta_3opt(productos):
    
    #Función principal que optimiza la ruta con 3-opt
    # Armar la ruta completa: E -> productos -> E
    ruta_completa = ["E"] + productos + ["E"]

    # Aplicar el algoritmo 3-opt
    ruta_optimizada = mejorar_con_3opt(ruta_completa)

    # Devolver la ruta completa (con E al principio y al final)
    return ruta_optimizada

# Endpoints estándar como L3 (/actuator/*)

@app.route('/actuator/health', methods=['GET'])
def actuator_health():
    #Health endpoint estándar como Spring Boot Actuator
    try:
        # Verificaciones basicas como L3
        matriz_ok = len(MATRIZ_DISTANCIAS) == 19
        
        # Prueba rapida del algoritmo
        try:
            ruta_test = optimizar_ruta_3opt(["P1", "P2"])
            algoritmo_ok = len(ruta_test) >= 4  # E,P1,P2,E
        except:
            algoritmo_ok = False
        
        status = "UP" if matriz_ok and algoritmo_ok else "DOWN"
        
        return jsonify({
            "status": status,
            "components": {
                "matriz": {"status": "UP" if matriz_ok else "DOWN"},
                "algoritmo": {"status": "UP" if algoritmo_ok else "DOWN"}
            }
        })
        
    except Exception as e:
        return jsonify({
            "status": "DOWN",
            "components": {
                "error": {"status": "DOWN", "details": str(e)}
            }
        }), 503

@app.route('/actuator/info', methods=['GET'])
def actuator_info():
    #Info endpoint como Spring Boot Actuator
    tiempo_activo = datetime.now() - inicio_servicio
    
    return jsonify({
        "app": {
            "name": "logistica-l2",
            "description": "Servicio de calculo de rutas con algoritmo 3-opt",
            "version": "1.0.0",
            "technology": "Python-Flask"
        },
        "build": {
            "algorithm": "3-opt",
            #"max_products": app.config['RECO_MAX_PRODUCTS'],
            "uptime_seconds": int(tiempo_activo.total_seconds())
        }
    })

# Mantener /up para compatibilidad con Docker Compose
@app.route('/up', methods=['GET'])
def health_simple():
    #Endpoint simple para Docker healthcheck
    return jsonify({"status": "OK"})

@app.route('/logistic/route', methods=['POST'])
def calcular_ruta():
    #Endpoint que calcula la ruta óptima
    try:
        # Obtener los datos del request
        datos = request.get_json()

        if not datos or 'items' not in datos:
            return jsonify({"error": "Falta el campo 'items'"}), 400

        items_str = datos['items']
        productos = []
        for item in items_str.split(','):
            producto_limpio = item.strip()
            if producto_limpio:  # solo agregar si no está vacío
                productos.append(producto_limpio)

        # Validaciones
        #if len(productos) > app.config['RECO_MAX_PRODUCTS']:
            #return jsonify({"error": f"Máximo {app.config['RECO_MAX_PRODUCTS']} productos permitidos"}), 400

        # Verificar que todos los productos existan en la bodega
        productos_invalidos = []
        for producto in productos:
            if producto not in NODOS_BODEGA:
                productos_invalidos.append(producto)
        
        if productos_invalidos:
            return jsonify({"error": f"Productos no encontrados: {productos_invalidos}"}), 400

        # Calcular ruta óptima
        ruta_optimizada = optimizar_ruta_3opt(productos)

        # Formato respuesta
        ruta_string = ",".join(ruta_optimizada)
        
        # Las metricas se manejan automaticamente por prometheus-flask-exporter

        return jsonify({
            "route": ruta_string
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    print("Iniciando servidor L2...")
    print(f"Puerto: {app.config['SERVER_PORT']}")
    print("Endpoints disponibles:")
    print("- GET /actuator/health")
    print("- GET /actuator/info") 
    print("- GET /metrics (prometheus)")
    print("- GET /up (docker)")
    print("- POST /logistic/route")
    app.run(debug=False, 
            host=app.config['SERVER_HOST'], 
            port=app.config['SERVER_PORT'])