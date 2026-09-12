package com.bootcamp.mini_project.kafka.consumer;

import com.bootcamp.mini_project.dto.transactions.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer component responsible for receiving sales transaction events
 * and feeding real-time metric updates to the analytics view.
 */
@Component
@Slf4j
public class TransactionConsumer {
    /**
     * Listens for transaction events and updates real-time metrics for dashboard pages.
     *
     * @param event the received {@link TransactionEvent} payload
     */
    @KafkaListener(topics = "transaction-events", groupId = "analytics-dashboard-group")
    public void consumeTransactionEvent(TransactionEvent event) {
        log.info("[ANALYTICS CONSUMER] Streamed transaction event code: {} | Total: {}", event.getTransactionCode(), event.getTotalAmount());
    }
}