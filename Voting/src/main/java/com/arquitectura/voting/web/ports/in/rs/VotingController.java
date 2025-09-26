package com.arquitectura.voting.web.ports.in.rs;

import com.arquitectura.voting.service.VotingService;
import com.arquitectura.voting.web.ports.in.request.VotingRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/voting")
public class VotingController {

    private static final int MAX_ITEMS = 10;

    private final VotingService votingService;

    @PostMapping
    public Mono<ResponseEntity<?>> vote(@RequestBody VotingRequest request) {

        log.info("Recibido el request: {}", request);

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Items list is required."));
        }

        // Normalizar CSV: quitar espacios y colapsar entradas vacías
        List<String> items = Arrays.stream(request.getItems().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        log.info("Empezando la verificación de integridad para los elementos: {}", items);
        // Verificación de hash de integridad si viene provisto
        String provided = request.getIntegrityHash();
        log.info("Hash de integridad provisto: {}", provided);
        if (provided != null && !provided.isBlank()) {
            String normalizedCsv = String.join(",", items);
            String computed = sha256Hex(normalizedCsv);
            log.info("Hash de integridad computado: {}", computed);
            if (!computed.equalsIgnoreCase(provided)) {
                log.warn("Desajuste de hash de integridad. esperado={}, provisto={}, normalizado={}", computed, provided, normalizedCsv);
                return Mono.just(ResponseEntity.badRequest().body("Desajuste de hash de integridad"));
            }
        }

        if (items.size() > MAX_ITEMS) {
            return Mono.just(ResponseEntity.badRequest().body("Se permiten un máximo de 10 elementos."));
        }

        Set<String> uniqueItems = new HashSet<>(items);
        if (uniqueItems.size() != items.size()) {
            return Mono.just(ResponseEntity.badRequest().body("Los elementos no deben repetirse."));
        }

        for (String item : items) {
            if (!item.matches("P([1-9]|1[0-8])")) {
                return Mono.just(ResponseEntity.badRequest().body("Elemento inválido: " + item));
            }
        }

        return votingService.processVote(request.getItems())
                .map(result -> ResponseEntity.status(200).body(result));
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular SHA-256", e);
        }
    }
}