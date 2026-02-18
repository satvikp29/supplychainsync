package com.supplychainsync.inventory_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventsListener {

    @KafkaListener(topics = "shipment.events", groupId = "inventory-service")
    public void onMessage(String message) {
        System.out.println("[inventory-service] received: " + message);
    }
}
