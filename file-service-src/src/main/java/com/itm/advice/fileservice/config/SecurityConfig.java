package com.itm.advice.fileservice.config;

import com.itm.advice.fileservice.exception.CustomAuthenticationEntryPoint;
import com.itm.advice.fileservice.exception.CustomAccessDeniedHandler;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final String[] WHITELIST_URLS = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    /**
     * For the backend-resources, I indicate that all the endpoints are protected.
     * To request any endpoint, the OAuth2 protocol is necessary, using the server configured and with the given scope.
     * Thus, a JWT will be used to communicate between the backend-resources and backend-auth when backend-resources
     * needs to validate the authentication of a request.
     */
    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(WHITELIST_URLS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> {oauth2
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .jwt()
                        .jwtAuthenticationConverter(SecurityConfig::convertJwtToken);
                });
        return http.build();
    }

    private static JwtAuthenticationToken convertJwtToken(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities);

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return new JwtAuthenticationToken(jwt, authorities, authenticationToken.getName());
    }
}