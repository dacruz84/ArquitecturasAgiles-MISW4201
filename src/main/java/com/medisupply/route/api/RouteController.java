package com.medisupply.route.api;

import com.medisupply.route.api.dto.RouteMultiRequest;
import com.medisupply.route.api.dto.RouteMultiResponse;
import com.medisupply.route.core.RoutePlannerService;
import com.medisupply.route.util.Hashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {
  private final RoutePlannerService planner;
  private final int maxProducts;

  private static final Pattern CSV_SPLIT = Pattern.compile("\\s*,\\s*");

  public RouteController(RoutePlannerService planner,
                         @Value("${reco.max-products:10}") int maxProducts,
                         @Value("${INSTANCE_ID:}") String iid,
                         @Value("${server.port}") int port) {
    this.planner = planner;
    this.maxProducts = maxProducts;
  }

  @PostMapping("/optimal-order")
  public ResponseEntity<?> optimal(@RequestBody RouteMultiRequest req){
    if (req.items() == null || req.items().isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error","Campo 'items' vacío"));
    }

    // Parsear CSV -> lista de puntos
    String[] tokens = CSV_SPLIT.split(req.items().trim());
    List<String> points = new ArrayList<>();
    for (String t : tokens) {
      if (!t.isBlank()) points.add(t.trim());
    }

    if (points.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error","Ningún item válido en 'items'"));
    }
    if (points.size() > maxProducts) {
      return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
          .body(Map.of("error","Máximo " + maxProducts + " productos"));
    }

    // Planificar
    var plan = planner.plan("E", points);
    String routeCsv = String.join(", ", plan.visitOrder());
    // Hash
    String hash = Hashing.sha256(routeCsv); 


    return ResponseEntity.ok(new RouteMultiResponse(
        routeCsv,
        plan.cost(),
        hash
    ));
  }
}
