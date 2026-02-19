package com.supplychainsync.inventory_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventsController {

    private final ShipmentEventRepository repo;

    public EventsController(ShipmentEventRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/events")
    public List<ShipmentEventEntity> allEvents() {
        return repo.findAll();
    }
}
