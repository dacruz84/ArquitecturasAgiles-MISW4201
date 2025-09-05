package com.arquitectura.voting.service;

import com.arquitectura.voting.web.ports.out.response.VotingResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class VotingService {

    // Add ObjectMapper as a field
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient webClient;
    private final String service1Url;
    private final String service2Url;
    private final String service3Url;

    public VotingService(
            WebClient.Builder webClientBuilder,
            @Value("${voting.service1.url}") String service1Url,
            @Value("${voting.service2.url}") String service2Url,
            @Value("${voting.service3.url}") String service3Url
    ) {
        this.webClient = webClientBuilder.build();
        this.service1Url = service1Url;
        this.service2Url = service2Url;
        this.service3Url = service3Url;
    }

    public Mono<VotingResponse> processVote(String items) {
        Flux<String> responses = Flux.merge(
                callService(service1Url, items),
                callService(service2Url, items),
                callService(service3Url, items)
        );

        Map<String, Integer> counts = new ConcurrentHashMap<>();
        AtomicBoolean decided = new AtomicBoolean(false);

        return responses
                .map(response -> {
                    try {
                        JsonNode node = objectMapper.readTree(response);
                        return node.get("route").asText();
                    } catch (Exception e) {
                        log.error("Error parsing response: {}", response, e);
                        return "error";
                    }
                })
                .filter(route -> !decided.get())
                .flatMap(route -> {
                    int count = counts.merge(route, 1, Integer::sum);

                    if (count >= 2 && decided.compareAndSet(false, true)) {
                        log.info("Llegamos al quórum con la respuesta: {}", route);
                        VotingResponse votingResponse = new VotingResponse();
                        votingResponse.setRoute(route);
                        return Mono.just(votingResponse);
                    }
                    return Mono.empty();
                })
                .next();
    }

    private Mono<String> callService(String url, String items) {
        return webClient.post()
                .uri(url)
                .body(BodyInserters.fromValue(Map.of("items", items)))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("error"));
    }
}