package com.zenika.enigma.chronobidule.central.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Store order facade should")
class StoreOrderFacadeTests {

    private MockRestServiceServer mockServer;
    @Mock
    private StoresRepository storesRepository;
    private StoreOrderFacade facade;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        var restClientBuilder = RestClient.builder(restTemplate);
        facade = new StoreOrderFacade(storesRepository, restClientBuilder);
    }

    @Test
    @DisplayName("refuse placing order for unknown store")
    void placeOrderUnknownStore() throws Exception {
        when(storesRepository.findById(123L)).thenReturn(Optional.empty());

        var order = new Order(1000L, 123L, List.of(
                new OrderItem(111L, 11),
                new OrderItem(222L, 22),
                new OrderItem(333L, 33)
        ));

        assertThrows(OrderStoreException.class, () -> facade.placeOrder(order));
    }

    @Test
    @DisplayName("place order with all quantities available to the store successfully")
    void placeOrderQuantitiesAvailable() throws Exception {
        var store = new Store(123L, "test store", "http://test-store.chronobidule.com/api");
        when(storesRepository.findById(123L)).thenReturn(Optional.of(store));

        var order = new Order(1000L, 123L, List.of(
                new OrderItem(111L, 11),
                new OrderItem(222L, 22),
                new OrderItem(333L, 33)
        ));

        var response = new ObjectMapper().writeValueAsString(Response.from(order));
        mockServer.expect(requestTo("http://test-store.chronobidule.com/api/store/orders"))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id", equalTo(1000)))
                .andExpect(jsonPath("items[0].productId", equalTo(111)))
                .andExpect(jsonPath("items[0].quantity", equalTo(11)))
                .andExpect(jsonPath("items[1].productId", equalTo(222)))
                .andExpect(jsonPath("items[1].quantity", equalTo(22)))
                .andExpect(jsonPath("items[2].productId", equalTo(333)))
                .andExpect(jsonPath("items[2].quantity", equalTo(33)))
                .andRespond(withStatus(HttpStatus.OK).body(response).contentType(APPLICATION_JSON));

        var actual = facade.placeOrder(order);
        assertThat(actual).isEqualTo(order);

        mockServer.verify();
    }

    @Test
    @DisplayName("place order with too many quantities requested to the store successfully")
    void placeOrderTooManyQuantities() throws Exception {
        var store = new Store(123L, "test store", "http://test-store.chronobidule.com/api");
        when(storesRepository.findById(123L)).thenReturn(Optional.of(store));

        var order = new Order(1000L, 123L, List.of(
                new OrderItem(111L, 11),
                new OrderItem(222L, 22),
                new OrderItem(333L, 33)
        ));
        var placedOrder = new Order(1000L, 123L, List.of(
                new OrderItem(111L, 1),
                new OrderItem(222L, 2),
                new OrderItem(333L, 3)
        ));

        var response = new ObjectMapper().writeValueAsString(Response.from(placedOrder));
        mockServer.expect(requestTo("http://test-store.chronobidule.com/api/store/orders"))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id", equalTo(1000)))
                .andExpect(jsonPath("items[0].productId", equalTo(111)))
                .andExpect(jsonPath("items[0].quantity", equalTo(11)))
                .andExpect(jsonPath("items[1].productId", equalTo(222)))
                .andExpect(jsonPath("items[1].quantity", equalTo(22)))
                .andExpect(jsonPath("items[2].productId", equalTo(333)))
                .andExpect(jsonPath("items[2].quantity", equalTo(33)))
                .andRespond(withStatus(HttpStatus.OK).body(response).contentType(APPLICATION_JSON));

        var actual = facade.placeOrder(order);
        assertThat(actual).isEqualTo(placedOrder);

        mockServer.verify();
    }

    private record Response(long id, List<ResponseItem> items) {
        static Response from(Order order) {
            return new Response(order.getId(), order.getItems().stream().map(ResponseItem::from).toList());
        }
    }

    private record ResponseItem(long productId, int quantity) {
        static ResponseItem from(OrderItem orderItem) {
            return new ResponseItem(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

}
