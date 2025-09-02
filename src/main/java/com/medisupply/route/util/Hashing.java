package com.medisupply.route.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hashing {
  public static String sha256(String s){
    try {
      MessageDigest d = MessageDigest.getInstance("SHA-256");
      byte[] h = d.digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : h) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) { throw new RuntimeException(e); }
  }
}
