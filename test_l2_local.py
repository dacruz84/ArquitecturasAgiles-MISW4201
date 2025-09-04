#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test básico de L2 - similar al estilo de L1
"""

from app import optimizar_ruta_3opt

def test_basico():
    """Test básico del algoritmo"""
    print("TEST BÁSICO L2")
    print("="*30)
    
    # Caso básico de prueba
    productos = ["P1", "P3", "P4"]
    
    print(f"Probando con: {productos}")
    
    # Ejecutar L2
    ruta = optimizar_ruta_3opt(productos)
    ruta_str = ",".join(ruta)
    
    print(f"Resultado: {ruta_str}")
    
    # Verificaciones básicas
    if ruta[0] == "E" and ruta[-1] == "E":
        print("Test exitoso")
    else:
        print("Test fallido")

if __name__ == "__main__":
    test_basico()
