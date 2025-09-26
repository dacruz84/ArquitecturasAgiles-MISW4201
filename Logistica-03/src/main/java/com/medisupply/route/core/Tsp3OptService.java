package com.medisupply.route.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Tsp3OptService {

  // Igual que el Python: límite bajo para evitar cuelgues
  @Value("${reco.max-improvements:100}")
  private int maxImprovements;

  public List<String> solveTour(String startEnd, List<String> mustVisit) {
    long t0 = System.nanoTime();

    // 1) Semilla: orden de entrada (preserva orden y quita duplicados)
    List<String> seq = new ArrayList<>(new LinkedHashSet<>(mustVisit));

    // 2) Construir tour con índices, E al inicio y fin
    int n = seq.size() + 2;
    int[] tour = new int[n];
    tour[0] = MatrixStatic.idx(startEnd);
    for (int i = 0; i < seq.size(); i++) tour[i + 1] = MatrixStatic.idx(seq.get(i));
    tour[n - 1] = MatrixStatic.idx(startEnd);

    double best = routeCost(tour);
    int improvements = 0;
    boolean improved = true;

    // 3) 3-opt "recortado" + FIRST-IMPROVEMENT (como el Python)
    while (improved && improvements < maxImprovements) {
      improved = false;

      outer:
      for (int i = 1; i < n - 3; i++) {
        for (int j = i + 1; j < n - 2; j++) {
          for (int k = j + 1; k < n - 1; k++) {

            // Caso 1: invertir [i..j-1]
            int[] t1 = tour.clone();
            reverse(t1, i, j - 1);
            double c1 = routeCost(t1);
            if (c1 + 1e-12 < best) {
              tour = t1; best = c1; improvements++; improved = true;
              break outer; // FIRST-IMPROVEMENT
            }

            // Caso 2: invertir [j..k-1]
            int[] t2 = tour.clone();
            reverse(t2, j, k - 1);
            double c2 = routeCost(t2);
            if (c2 + 1e-12 < best) {
              tour = t2; best = c2; improvements++; improved = true;
              break outer;
            }

            // Caso 3: invertir ambos (secuencial)
            int[] t3 = tour.clone();
            reverse(t3, i, j - 1);
            reverse(t3, j, k - 1);
            double c3 = routeCost(t3);
            if (c3 + 1e-12 < best) {
              tour = t3; best = c3; improvements++; improved = true;
              break outer;
            }
          }
        }
      }
    }

    long t1 = System.nanoTime();
    System.out.printf(
        Locale.ROOT,
        "3opt(first-impr,3-casos): n=%d, improvements=%d, time=%.3f ms, cost=%.3f%n",
        n, improvements, (t1 - t0) / 1e6, best
    );

    // 4) Devolver nombres
    List<String> out = new ArrayList<>(n);
    for (int v : tour) out.add(MatrixStatic.NODES.get(v));
    return out;
  }

  /** Suma de distancias consecutivas del tour */
  private double routeCost(int[] t) {
    double s = 0.0;
    for (int i = 0; i < t.length - 1; i++) s += MatrixStatic.d(t[i], t[i + 1]);
    return s;
  }

  /** Invierte el segmento inclusivo [a..b] */
  private void reverse(int[] t, int a, int b) {
    while (a < b) { int x = t[a]; t[a] = t[b]; t[b] = x; a++; b--; }
  }
}
