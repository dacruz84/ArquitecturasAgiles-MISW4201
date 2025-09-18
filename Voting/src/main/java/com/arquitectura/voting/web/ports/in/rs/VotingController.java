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

        log.info("Received voting request: {}", request);

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Items list is required."));
        }

        List<String> items = Arrays.stream(request.getItems().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (items.size() > MAX_ITEMS) {
            return Mono.just(ResponseEntity.badRequest().body("Maximum 10 items allowed."));
        }

        Set<String> uniqueItems = new HashSet<>(items);
        if (uniqueItems.size() != items.size()) {
            return Mono.just(ResponseEntity.badRequest().body("Items must not be repeated."));
        }

        for (String item : items) {
            if (!item.matches("P([1-9]|1[0-8])")) {
                return Mono.just(ResponseEntity.badRequest().body("Invalid item: " + item));
            }
        }

        return votingService.processVote(request.getItems())
                .map(result -> ResponseEntity.status(200).body(result));
    }
}