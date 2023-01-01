package com.example.demo.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public class Subscription {
    private UUID subscriber;
    private List<UUID> producers = new ArrayList<>();

    public void addProducer(UUID producerId) {
        this.producers.add(producerId);
    }

    public UUID getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(UUID subscriber) {
        this.subscriber = subscriber;
    }

    public List<UUID> getProducers() {
        return producers;
    }

    public void setProducers(List<UUID> producers) {
        this.producers = producers;
    }

    public Mono<ResponseEntity<Map<String, Object>>> getSubscriptionsForSubscriberById(UUID subscriberId) {
        return null;
    }
}
