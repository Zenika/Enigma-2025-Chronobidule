package com.zenika.enigma.chronobidule.central.prices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.enigma.chronobidule.central.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@DisplayName("Store price facade should")
class StorePriceFacadeTests {

    private MockRestServiceServer mockServer;
    private StorePriceFacade facade;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        var restClientBuilder = RestClient.builder(restTemplate);
        facade = new StorePriceFacade(restClientBuilder);
    }

    @Test
    @DisplayName("send prices to the store successfully")
    void sendPrices() throws Exception {
        var store = new Store(123L, "test store", "http://test-store.chronobidule.com/api");
        var prices = List.of(
                StoreProductPrice.of(store, 111L, BigDecimal.valueOf(12.34)),
                StoreProductPrice.of(store, 222L, BigDecimal.valueOf(56.78)),
                StoreProductPrice.of(store, 333L, BigDecimal.valueOf(9))
        );

        var response = new ObjectMapper().writeValueAsString(new Response(prices));
        mockServer.expect(requestTo("http://test-store.chronobidule.com/api/store/prices"))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("prices[0].productId", equalTo(111)))
                .andExpect(jsonPath("prices[0].amount", equalTo(12.34)))
                .andExpect(jsonPath("prices[1].productId", equalTo(222)))
                .andExpect(jsonPath("prices[1].amount", equalTo(56.78)))
                .andExpect(jsonPath("prices[2].productId", equalTo(333)))
                .andExpect(jsonPath("prices[2].amount", equalTo(9)))
                .andRespond(withStatus(HttpStatus.OK).body(response).contentType(APPLICATION_JSON));

        facade.sendPricesToStore(store, prices);
        mockServer.verify();
    }

    private record Response(List<StoreProductPrice> prices) {
    }

}
