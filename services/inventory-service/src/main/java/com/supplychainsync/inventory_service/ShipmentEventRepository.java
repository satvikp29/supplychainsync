package com.supplychainsync.inventory_service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEventEntity, Long> {
}
