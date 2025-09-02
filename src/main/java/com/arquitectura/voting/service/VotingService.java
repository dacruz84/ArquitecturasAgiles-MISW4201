package com.arquitectura.voting.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VotingService {

    public Mono<String> processVote(String items) {
        return Mono.just("String processed: " + items);
    }
}
