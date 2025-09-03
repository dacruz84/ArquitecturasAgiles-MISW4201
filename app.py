from flask import Flask, request, jsonify
from flask_cors import CORS
import random
import time
import math

app = Flask(__name__)
CORS(app)

# Matriz de distancias basada en coordenadas de bodega
# E = entrada, P1-P20 = productos en diferentes ubicaciones
WAREHOUSE_COORDS = {
    "E": (0, 0),      # Entrada/salida
    "P1": (1, 1), "P2": (2, 1), "P3": (3, 1), "P4": (4, 1), "P5": (5, 1),
    "P6": (1, 2), "P7": (2, 2), "P8": (3, 2), "P9": (4, 2), "P10": (5, 2),
    "P11": (1, 3), "P12": (2, 3), "P13": (3, 3), "P14": (4, 3), "P15": (5, 3),
    "P16": (1, 4), "P17": (2, 4), "P18": (3, 4), "P19": (4, 4), "P20": (5, 4)
}

def calculate_distance(product1, product2):
    """Calcula distancia euclidiana real entre dos productos"""
    if product1 not in WAREHOUSE_COORDS or product2 not in WAREHOUSE_COORDS:
        return 999.0  # Distancia muy alta para productos no válidos

    x1, y1 = WAREHOUSE_COORDS[product1]
    x2, y2 = WAREHOUSE_COORDS[product2]

    return math.sqrt((x2 - x1)**2 + (y2 - y1)**2)

def calculate_tour_distance(tour):
    """Calcula distancia total de una ruta"""
    total_distance = 0.0
    for i in range(len(tour) - 1):
        total_distance += calculate_distance(tour[i], tour[i + 1])
    return total_distance

def reverse_segment(tour, start, end):
    """Invierte un segmento de la ruta"""
    new_tour = tour.copy()
    new_tour[start:end+1] = reversed(new_tour[start:end+1])
    return new_tour

def three_opt_improve(tour):
    """
    Implementación del algoritmo 3-opt
    Basado en la lógica del proyecto Java
    """
    best_tour = tour.copy()
    best_distance = calculate_tour_distance(best_tour)

    n = len(tour)
    max_improvements = 100
    improvements = 0

    improved = True
    while improved and improvements < max_improvements:
        improved = False

        # Revisar todas las combinaciones posibles de 3 segmentos
        for i in range(1, n - 3):
            for j in range(i + 1, n - 2):
                for k in range(j + 1, n - 1):

                    # Caso 1: Reversear segmento i->j-1
                    new_tour = reverse_segment(tour, i, j-1)
                    new_distance = calculate_tour_distance(new_tour)

                    if new_distance < best_distance:
                        best_tour = new_tour
                        best_distance = new_distance
                        improved = True
                        improvements += 1
                        break

                    # Caso 2: Reversear segmento j->k-1
                    new_tour = reverse_segment(tour, j, k-1)
                    new_distance = calculate_tour_distance(new_tour)

                    if new_distance < best_distance:
                        best_tour = new_tour
                        best_distance = new_distance
                        improved = True
                        improvements += 1
                        break

                    # Caso 3: Reversear ambos segmentos
                    new_tour = reverse_segment(tour, i, j-1)
                    new_tour = reverse_segment(new_tour, j, k-1)
                    new_distance = calculate_tour_distance(new_tour)

                    if new_distance < best_distance:
                        best_tour = new_tour
                        best_distance = new_distance
                        improved = True
                        improvements += 1
                        break

                if improved:
                    break
            if improved:
                break

        tour = best_tour.copy()

    print(f"3-opt: improvements={improvements}, final_distance={best_distance:.2f}")
    return best_tour

def optimize_route_3opt(products):
    """
    Optimiza la ruta usando algoritmo 3-opt real
    """
    # Crear ruta completa: E -> productos -> E
    tour = ["E"] + products + ["E"]

    # Aplicar 3-opt para optimizar
    optimized_tour = three_opt_improve(tour)

    # Remover puntos de entrada/salida para la respuesta
    # (solo devolver el orden de los productos)
    return optimized_tour[1:-1]  # Quitar E inicial y final

@app.route('/health', methods=['GET'])
def health_check():
    """Endpoint de salud"""
    return jsonify({
        "status": "OK",
        "service": "Logistica-L2",
        "technology": "Python-Flask",
        "algorithm": "3-opt Real"
    })

@app.route('/calculate-route', methods=['POST'])
def calculate_route():
    """Endpoint principal para calcular ruta óptima con 3-opt"""
    try:
        start_time = time.time()

        # Obtener datos de entrada
        data = request.get_json()

        if not data or 'items' not in data:
            return jsonify({"error": "Missing 'items' field"}), 400

        # Parsear lista de productos
        items_str = data['items']
        products = [item.strip() for item in items_str.split(',')]

        # Validar productos
        if len(products) > 10:
            return jsonify({"error": "Maximum 10 products allowed"}), 400

        # Validar que todos los productos existen en el warehouse
        for product in products:
            if product not in WAREHOUSE_COORDS:
                return jsonify({"error": f"Product {product} not found in warehouse"}), 400

        # Calcular ruta óptima usando algoritmo 3-opt REAL
        optimized_products = optimize_route_3opt(products)

        end_time = time.time()
        processing_time = round(end_time - start_time, 3)

        return jsonify({
            "optimizedRoute": ",".join(optimized_products),
            "originalItems": items_str,
            "processingTime": f"{processing_time}s",
            "algorithm": "3-opt",
            "totalProducts": len(products)
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)