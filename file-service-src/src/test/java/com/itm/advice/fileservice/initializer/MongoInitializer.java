package com.itm.advice.fileservice.initializer;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.itm.advice.fileservice.util.PropertyConstants.MONGO_URI;

@Testcontainers
public class MongoInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext){
        mongoDBContainer.start();
        System.setProperty(MONGO_URI, mongoDBContainer.getReplicaSetUrl());
    }
}
