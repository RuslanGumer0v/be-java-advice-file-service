package com.itm.advice.fileservice.kafka.producer;

import com.itm.space.itmadvicecommonmodels.kafka.model.FileEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileProducer implements EventProducer<FileEvent> {

    private final KafkaTemplate<String, FileEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.file-events}")
    private String topic;

    @Override
    public void produce(FileEvent event) {
        log.info("Producing file event: {}", event);
        kafkaTemplate.send(topic, event);
    }
}
