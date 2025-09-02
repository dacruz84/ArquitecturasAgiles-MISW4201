package com.medisupply.route.api.dto;


public record RouteMultiResponse(
  String route,
  double cost,
  String hash
) {}
