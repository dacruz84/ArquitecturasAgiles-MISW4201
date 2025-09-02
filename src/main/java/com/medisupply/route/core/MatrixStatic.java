package com.medisupply.route.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MatrixStatic {
  private MatrixStatic() {}

  public static final List<String> NODES = List.of(
    "E", "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9",
    "P10", "P11", "P12", "P13", "P14", "P15", "P16", "P17", "P18"
  );

  public static final Map<String,Integer> INDEX = new HashMap<>();
  static { for (int i = 0; i < NODES.size(); i++) INDEX.put(NODES.get(i), i); }

  public static int idx(String name) {
    Integer i = INDEX.get(name);
    if (i == null) throw new IllegalArgumentException("Nodo desconocido: " + name);
    return i;
  }

  public static final double[][] DIST = new double[][]{
    {0, 1, 2, 3, 4, 3, 2, 3, 4, 5, 6, 5, 4, 5, 6, 7, 8, 7, 6},
    {1, 0, 1, 2, 3, 2, 1, 2, 3, 4, 5, 4, 3, 4, 5, 6, 7, 6, 5},
    {2, 1, 0, 1, 2, 3, 2, 3, 4, 3, 4, 5, 4, 5, 6, 5, 6, 7, 6},
    {3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 3, 4, 5, 6, 5, 4, 5, 6, 7},
    {4, 3, 2, 1, 0, 1, 2, 3, 2, 1, 2, 3, 4, 5, 4, 3, 4, 5, 6},
    {3, 2, 3, 2, 1, 0, 1, 2, 3, 2, 3, 4, 3, 4, 5, 4, 5, 6, 5},
    {2, 1, 2, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 3, 4, 5, 6, 5, 4},
    {3, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 2, 1, 2, 3, 4, 5, 4, 3},
    {4, 3, 4, 3, 2, 3, 2, 1, 0, 1, 2, 3, 2, 3, 4, 3, 4, 5, 4},
    {5, 4, 3, 2, 1, 2, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 3, 4, 5},
    {6, 5, 4, 3, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 2, 1, 2, 3, 4},
    {5, 4, 5, 4, 3, 4, 3, 2, 3, 2, 1, 0, 1, 2, 3, 2, 3, 4, 3},
    {4, 3, 4, 5, 4, 3, 2, 1, 2, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2},
    {5, 4, 5, 6, 5, 4, 3, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 2, 1},
    {6, 5, 6, 5, 4, 5, 4, 3, 4, 3, 2, 3, 2, 1, 0, 1, 2, 3, 2},
    {7, 6, 5, 4, 3, 4, 5, 4, 3, 2, 1, 2, 3, 2, 1, 0, 1, 2, 3},
    {8, 7, 6, 5, 4, 5, 6, 5, 4, 3, 2, 3, 4, 3, 2, 1, 0, 1, 2},
    {7, 6, 7, 6, 5, 6, 5, 4, 5, 4, 3, 4, 3, 2, 3, 2, 1, 0, 1},
    {6, 5, 6, 7, 6, 5, 4, 3, 4, 5, 4, 3, 2, 1, 2, 3, 2, 1, 0}
  };

  public static double d(int i, int j) { return DIST[i][j]; }

  // Conveniencia para no tocar otras clases
  public static double d(String a, String b) { return d(idx(a), idx(b)); }
}
