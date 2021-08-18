package com.example.keycloakspringbootmicroservice.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class InstanceFactory {

    private final Environment environment;

    public InstanceFactory(Environment environment) {
        this.environment = environment;
    }

    public Keycloak getKeycloakInstance() {
        String passwordProperty = environment.getProperty("keycloak.credentials.secret");
        return KeycloakBuilder.builder()
            .serverUrl(environment.getProperty("keycloak.auth-server-url"))
            .realm(environment.getProperty("keycloak.realm"))
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(environment.getProperty("keycloak.resource"))
            .clientSecret(passwordProperty.isEmpty() ? null : passwordProperty)
            .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
            .build();
    }
}
