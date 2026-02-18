package com.supplychainsync.shipment_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShipmentController {

    private final ShipmentEventPublisher publisher;

    public ShipmentController(ShipmentEventPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping("/health")
    public String health() {
        return "Shipment Service is running";
    }

    @GetMapping("/publish")
    public String publish(@RequestParam(defaultValue = "ShipmentCreated") String msg) {
        publisher.publish(msg);
        return "Published: " + msg;
    }
}
