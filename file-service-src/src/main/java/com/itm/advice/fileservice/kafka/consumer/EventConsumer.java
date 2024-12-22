package com.itm.advice.fileservice.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Слушатель событий
 */
public interface EventConsumer {

    void consume(ConsumerRecord<String, byte[]> consumerRecord);
}
