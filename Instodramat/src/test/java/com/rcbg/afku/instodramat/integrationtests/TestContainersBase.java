package com.rcbg.afku.instodramat.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;

import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestContainersBase {

    private final RestTemplate restTemplate = new RestTemplate();
    static MySQLContainer<?> mysql;
    static KeycloakContainer keycloak;

    static {
        mysql = new MySQLContainer<>("mysql:latest")
                .withPassword("testPassword")
                .withUsername("user")
                .withDatabaseName("instodramat-test");
        mysql.start();

        keycloak = new KeycloakContainer().withRealmImportFile("instodramat-realm-keycloakconfig-testcontainer.json");
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry){
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "realms/instodramat");
        registry.add("keycloak.enabled", () -> true);
    }

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysql.getUsername());
        registry.add("spring.datasource.password", () -> mysql.getPassword());
    }

    protected String obtainJwtTokenResponse(String username, String password){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("username", username);
            formData.add("password", password);
            formData.add("client_id", "instodramat");
            formData.add("grant_type", "password");
            formData.add("client_secret", "hello");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(getKeycloakRealmBaseUrl() + "/protocol/openid-connect/token", request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new AuthenticationException("Cannot obtain JWT token");
            }
            return (String) new ObjectMapper().readValue(response.getBody(), HashMap.class).get("access_token");
        } catch (AuthenticationException | JsonProcessingException e){
            Assertions.fail("Cannot get JWT token");
        }
        return null;
    }

    protected String getKeycloakRealmBaseUrl(){
        return keycloak.getAuthServerUrl() + "realms/instodramat";
    }
}
