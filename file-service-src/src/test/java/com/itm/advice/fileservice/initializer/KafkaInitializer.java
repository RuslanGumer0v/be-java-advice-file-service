package com.itm.advice.fileservice.initializer;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.itm.advice.fileservice.util.PropertyConstants.KAFKA_BOOTSTRAP_SERVERS;

@Testcontainers
public class KafkaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3"))
            .withExposedPorts(9093)
            .withReuse(false);

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext){
        kafkaContainer.start();
        System.setProperty(KAFKA_BOOTSTRAP_SERVERS, kafkaContainer.getBootstrapServers());
    }
}