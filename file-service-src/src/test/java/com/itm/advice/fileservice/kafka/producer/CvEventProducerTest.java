package com.itm.advice.fileservice.kafka.producer;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.space.itmadvicecommonmodels.kafka.model.CvEvent;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


class CvEventProducerTest extends BaseIntegrationTest {

    @Autowired
    private CvEventProducer cvEventProducer;

    @Value("${spring.kafka.topic.cv-events}")
    private String topic;

    @DisplayName("Отправляет и проверяет событие резюме в Kafka")
    @Test
    @SneakyThrows
    void produce() {
        CvEvent event = jsonParserUtil.getObjectFromJson(
                "/json/CvEvent.json", CvEvent.class);

        cvEventProducer.produce(event);

        byte[] serializedEvent = new ObjectMapper().writeValueAsBytes(event);

        testConsumerService.consumeAndValidate(topic, serializedEvent);
    }
}