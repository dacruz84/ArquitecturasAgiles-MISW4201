package com.medisupply.route.infra;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FailureFilter extends OncePerRequestFilter {
  @Value("${reco.failure-rate:0.0}") private double rate;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    if (req.getRequestURI().startsWith("/api/")) {
      if (ThreadLocalRandom.current().nextDouble() < rate) {
        res.setStatus(500);
        res.setHeader("X-Simulated-Failure", "true");
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"Simulated failure\"}");
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
