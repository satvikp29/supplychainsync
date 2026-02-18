package com.supplychainsync.shipment_service;

import org.springframework.kafka.core.KafkaTemplate;

public class ShipmentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "shipment.events";

    public ShipmentEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
