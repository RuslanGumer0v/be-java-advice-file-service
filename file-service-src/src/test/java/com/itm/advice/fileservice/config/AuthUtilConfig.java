package com.itm.advice.fileservice.config;


import com.itm.advice.fileservice.initializer.KeycloakInitializer;
import com.itm.space.itmadvicecommonmodels.utils.TestAuthUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthUtilConfig {

    private static final String REALM = "ITM-Advice";
    private static final String CLIENT_ID = "gateway";
    private static final String CLIENT_SECRET = "jpnYK5CvskQbjY2B9W7j8rfU5oLiaU5R";
    private static final String DEFAULT_PASSWORD = "passwd";

    @Bean
    public TestAuthUtil authUtil() {

        String keycloakUrl = KeycloakInitializer.container.getAuthServerUrl();
        return new TestAuthUtil(keycloakUrl, REALM, CLIENT_ID, CLIENT_SECRET, DEFAULT_PASSWORD);
    }
}
