package com.zenika.enigma.chronobidule.central.orders;

import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class StoreOrderFacade {

    private final StoresRepository storesRepository;
    private final RestClient.Builder restClientBuilder;

    public StoreOrderFacade(StoresRepository storesRepository, RestClient.Builder restClientBuilder) {
        this.storesRepository = storesRepository;
        this.restClientBuilder = restClientBuilder;
    }

    Order placeOrder(Order order) {
        try {
            var store = storesRepository.findById(order.getStoreId());
            if (store.isEmpty()) {
                throw new IllegalArgumentException("Cannot place order for unknown store " + order.getStoreId());
            }
            var restClient = restClientBuilder.baseUrl(store.get().getBaseUrl()).build();
            var placedOrder = restClient.post()
                    .uri("/store/orders")
                    .contentType(APPLICATION_JSON)
                    .body(PlaceOrderInteraction.from(order))
                    .retrieve()
                    .toEntity(PlaceOrderInteraction.class);
            return placedOrder.getBody().convert(order.getStoreId());
        } catch (Throwable throwable) {
            throw new OrderStoreException("place order", order.getStoreId(), "unexpected error while placing order", throwable);
        }
    }

    public BigDecimal payOrder(Order order) {
        try {
            var store = storesRepository.findById(order.getStoreId());
            if (store.isEmpty()) {
                throw new IllegalArgumentException("Cannot pay order for unknown store " + order.getStoreId());
            }
            var restClient = restClientBuilder.baseUrl(store.get().getBaseUrl()).build();
            var payment = restClient.post()
                    .uri("/store/orders/{id}/payments", order.getId())
                    .retrieve()
                    .toEntity(PayOrderResponse.class);
            return payment.getBody().totalAmount();
        } catch (Throwable throwable) {
            throw new OrderStoreException("pay order", order.getStoreId(), "unexpected error while paying order", throwable);
        }
    }

    private record PlaceOrderInteraction(long id, List<OrderItemDto> items) {
        public static PlaceOrderInteraction from(Order order) {
            return new PlaceOrderInteraction(order.getId(), order.getItems().stream().map(OrderItemDto::from).toList());
        }

        public Order convert(long storeId) {
            return new Order(id, storeId, items.stream().map(OrderItemDto::convert).toList());
        }
    }

    private record OrderItemDto(long productId, int quantity) {
        public static OrderItemDto from(OrderItem item) {
            return new OrderItemDto(item.getProductId(), item.getQuantity());
        }

        public OrderItem convert() {
            return new OrderItem(productId, quantity);
        }
    }

    private record PayOrderResponse(BigDecimal totalAmount) {
    }
}
