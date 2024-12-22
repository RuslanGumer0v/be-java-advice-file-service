package com.itm.advice.fileservice.kafka.producer;

import com.itm.space.itmadvicecommonmodels.kafka.model.CvEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvEventProducer implements EventProducer<CvEvent> {

    private final KafkaTemplate<String,CvEvent>kafkaTemplate;

    @Value("${spring.kafka.topic.cv-events}")
    private String topic;

    @Override
    public void produce(CvEvent event) {
        log.info("Producing CV event: {}", event);
        kafkaTemplate.send(topic, event);
    }
}
