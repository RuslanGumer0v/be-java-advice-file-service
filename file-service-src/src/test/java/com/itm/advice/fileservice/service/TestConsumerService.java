package com.itm.advice.fileservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class TestConsumerService {

    private final ConsumerFactory<String, Object> consumerFactory;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @SneakyThrows
    public void consumeAndValidate(String topic, Object event) {
        try (Consumer<String, Object> consumer = consumerFactory.createConsumer(groupId, null)) {
            consumer.subscribe(Collections.singleton(topic));
            consumer.poll(Duration.ofSeconds(1));
            consumer.seekToBeginning(consumer.assignment());

            ConsumerRecords<String, Object> events = consumer.poll(Duration.ofSeconds(1));

            assertThat(events)
                    .hasSizeGreaterThan(0)
                    .extracting(ConsumerRecord::value)
                    .contains(event);
        }
    }
}
