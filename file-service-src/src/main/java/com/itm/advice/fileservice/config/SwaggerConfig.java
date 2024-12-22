package com.itm.advice.fileservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Advisement Service",
                description = "Данный сервис отвечает за работу с консультациями",
                version = "${springdoc.swagger-ui.version}"
        ),
        servers = {
                @Server(
                        description = "Локальное окружение",
                        url = "http://localhost:${server.port}"
                ),
                @Server(
                        description = "Dev окружение",
                        url = "${springdoc.swagger-ui.path}"
                )
        }
)
@SecurityScheme(
        name = "keycloak_oath_scheme",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        tokenUrl = "${spring.keycloak.auth-server-url}" + "/realms/" + "${spring.keycloak.realm}" + "/protocol/openid-connect/token"
                )
        )
)
public class SwaggerConfig {
}

