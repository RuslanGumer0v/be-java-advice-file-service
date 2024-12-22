package com.itm.advice.fileservice.initializer;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class KeycloakInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String KEYCLOAK_IMAGE_NAME = "quay.io/keycloak/keycloak:20.0";

    @Container
    public static final KeycloakContainer container = new KeycloakContainer(KEYCLOAK_IMAGE_NAME)
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withRealmImportFile("keycloak/keycloak-realm.json");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        container.start();
        System.out.println(container.getHttpPort());
        TestPropertyValues.of(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:" + container.getHttpPort() + "/realms/ITM-Advice",
                "keycloak.port=" + container.getHttpPort()
        ).applyTo(applicationContext.getEnvironment());
    }
}