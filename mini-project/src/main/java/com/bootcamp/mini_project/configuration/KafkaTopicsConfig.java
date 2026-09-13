package com.bootcamp.mini_project.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configures Kafka topics required for message publishing and consumption.
 */
@Configuration
public class KafkaTopicsConfig {
    /**
     * Defines the topic configuration for transaction events.
     *
     * @return a configured {@link NewTopic} instance for transactions
     */
    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("transaction-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}