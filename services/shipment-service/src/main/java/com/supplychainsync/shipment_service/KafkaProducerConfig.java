package com.supplychainsync.shipment_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ShipmentEventPublisher shipmentEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        return new ShipmentEventPublisher(kafkaTemplate);
    }
}
