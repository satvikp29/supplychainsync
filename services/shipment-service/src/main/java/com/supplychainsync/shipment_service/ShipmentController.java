package com.supplychainsync.shipment_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShipmentController {

    @GetMapping("/health")
    public String health() {
        return "Shipment Service is running";
    }
}
