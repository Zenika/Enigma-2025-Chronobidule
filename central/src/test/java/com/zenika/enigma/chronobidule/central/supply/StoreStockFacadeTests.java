package com.zenika.enigma.chronobidule.central.supply;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.enigma.chronobidule.central.products.Product;
import com.zenika.enigma.chronobidule.central.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@DisplayName("Store stock facade should")
class StoreStockFacadeTests {

    private MockRestServiceServer mockServer;
    private StoreStockFacade facade;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        var restClientBuilder = RestClient.builder(restTemplate);
        facade = new StoreStockFacade(restClientBuilder);
    }

    @Test
    @DisplayName("send stock to the store successfully")
    void sendStock() throws Exception {
        var store = new Store(123L, "test store", "http://test-store.chronobidule.com/api");
        var stock = List.of(
                StoreStockEntry.of(store, new Product(111L, "product 1"), 100),
                StoreStockEntry.of(store, new Product(222L, "product 2"), 200),
                StoreStockEntry.of(store, new Product(333L, "product 3"), 300)
        );

        var response = new ObjectMapper().writeValueAsString(new Response(stock));
        mockServer.expect(requestTo("http://test-store.chronobidule.com/api/store/stock"))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("stock[0].productId", equalTo(111)))
                .andExpect(jsonPath("stock[0].productName", equalTo("product 1")))
                .andExpect(jsonPath("stock[0].quantity", equalTo(100)))
                .andExpect(jsonPath("stock[1].productId", equalTo(222)))
                .andExpect(jsonPath("stock[1].productName", equalTo("product 2")))
                .andExpect(jsonPath("stock[1].quantity", equalTo(200)))
                .andExpect(jsonPath("stock[2].productId", equalTo(333)))
                .andExpect(jsonPath("stock[2].productName", equalTo("product 3")))
                .andExpect(jsonPath("stock[2].quantity", equalTo(300)))
                .andRespond(withStatus(HttpStatus.OK).body(response).contentType(APPLICATION_JSON));

        facade.sendStockToStore(store, stock);
        mockServer.verify();
    }

    private record Response(List<StoreStockEntry> stock) {
    }

}
