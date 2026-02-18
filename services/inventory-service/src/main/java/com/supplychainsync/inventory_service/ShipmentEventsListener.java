package com.supplychainsync.inventory_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventsListener {

    private final ShipmentEventRepository repo;

    public ShipmentEventsListener(ShipmentEventRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "shipment.events", groupId = "inventory-service")
    public void onMessage(String message) {
        repo.save(new ShipmentEventEntity(message));
        System.out.println("[inventory-service] saved event: " + message);
    }
}
