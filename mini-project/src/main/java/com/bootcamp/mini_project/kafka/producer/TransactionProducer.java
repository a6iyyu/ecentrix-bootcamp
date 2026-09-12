package com.bootcamp.mini_project.kafka.producer;

import com.bootcamp.mini_project.dto.transactions.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {
    private static final String TOPIC_TRANSACTIONS = "transaction-events";
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    /**
     * Publishes a created transaction event to the Kafka topic.
     *
     * @param event the transaction event payload to publish
     */
    public void sendTransactionEvent(TransactionEvent event) {
        log.info("[KAFKA PRODUCER] Publishing transaction event for code: {}", event.getTransactionCode());
        kafkaTemplate.send(TOPIC_TRANSACTIONS, event.getTransactionCode(), event);
    }
}