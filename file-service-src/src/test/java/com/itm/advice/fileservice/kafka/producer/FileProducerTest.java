package com.itm.advice.fileservice.kafka.producer;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.space.itmadvicecommonmodels.kafka.model.FileEvent;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


public class FileProducerTest extends BaseIntegrationTest {

    @Autowired
    private FileProducer fileProducer;

    @Value("${spring.kafka.topic.file-events}")
    private String topic;

    @DisplayName("Отправляет и проверяет сообщение")
    @Test
    @SneakyThrows
    void sendEventToKafka() {
        FileEvent event = jsonParserUtil.getObjectFromJson(
                "/json/FileEvent.json", FileEvent.class);

        fileProducer.produce(event);

        byte[] serializedEvent = new ObjectMapper().writeValueAsBytes(event);

        testConsumerService.consumeAndValidate(topic, serializedEvent);
    }
}
