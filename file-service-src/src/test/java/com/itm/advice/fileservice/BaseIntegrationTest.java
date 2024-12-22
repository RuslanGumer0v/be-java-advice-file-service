package com.itm.advice.fileservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itm.advice.fileservice.config.AuthUtilConfig;
import com.itm.advice.fileservice.initializer.KafkaInitializer;
import com.itm.advice.fileservice.initializer.KeycloakInitializer;
import com.itm.advice.fileservice.initializer.MongoInitializer;
import com.itm.advice.fileservice.service.TestConsumerService;
import com.itm.space.itmadvicecommonmodels.utils.JsonParserUtil;
import com.itm.space.itmadvicecommonmodels.utils.TestAuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(initializers = {
        KafkaInitializer.class,
        KeycloakInitializer.class,
        MongoInitializer.class
})
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Import(AuthUtilConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected TestAuthUtil authUtil;

    protected JsonParserUtil jsonParserUtil = new JsonParserUtil(new ObjectMapper().registerModule(new JavaTimeModule()));

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestConsumerService testConsumerService;
}
