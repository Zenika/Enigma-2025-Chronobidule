package com.zenika.enigma.chronobidule.store.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BootstrapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapService.class);

    private final RestClient restClient;

    public BootstrapService(RestClient.Builder restClientBuilder, @Value("${chronobidule.central.baseUrl}") String baseUrl) {
        LOGGER.info("Start bootstrap service with base URL to central {}", baseUrl);
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @EventListener
    public void registerToCentral(ApplicationReadyEvent ready) {
        LOGGER.info("Registering to central server");
        var registration = restClient.post()
                .uri("/central/stores")
                .body("""
                        {"name": "test store", "baseUrl": "http://localhost:9090"}
                        """)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, response) -> LOGGER.error("Could not register to central due to {} error", response.getStatusCode())
                )
                .body(Registration.class);
        LOGGER.info("Successfully registered to central as '{}' with id {}", registration.name, registration.id);
    }

    private record Registration(Long id, String name) {

    }

}
