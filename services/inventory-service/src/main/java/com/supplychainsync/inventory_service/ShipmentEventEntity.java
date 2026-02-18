package com.supplychainsync.inventory_service;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shipment_events")
public class ShipmentEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant receivedAt;

    protected ShipmentEventEntity() {
    }

    public ShipmentEventEntity(String message) {
        this.message = message;
        this.receivedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
