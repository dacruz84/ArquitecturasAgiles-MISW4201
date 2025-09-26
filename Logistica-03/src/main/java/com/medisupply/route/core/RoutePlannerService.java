package com.medisupply.route.core;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoutePlannerService {
  private final Tsp3OptService tsp;
  public RoutePlannerService(Tsp3OptService tsp){ this.tsp = tsp; }

  public record Plan(List<String> visitOrder, double cost) {}

  public Plan plan(String startEnd, List<String> points) {
    if (points==null || points.isEmpty()) throw new IllegalArgumentException("Lista de productos vacía");
    for (String p : points) if (!MatrixStatic.NODES.contains(p)) throw new IllegalArgumentException("Nodo no definido en matriz: " + p);

    List<String> tour = tsp.solveTour(startEnd, points);
    double total = 0.0;
    for (int i=0;i<tour.size()-1;i++) total += MatrixStatic.d(tour.get(i), tour.get(i+1));
    return new Plan(tour, total);
  }
}
