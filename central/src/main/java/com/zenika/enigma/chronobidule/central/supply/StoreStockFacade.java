package com.zenika.enigma.chronobidule.central.supply;

import com.zenika.enigma.chronobidule.central.stores.Store;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class StoreStockFacade {

    private final RestClient.Builder restClientBuilder;

    public StoreStockFacade(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public void sendStockToStore(Store store, Collection<StoreStockEntry> storeStock) {
        var restClient = restClientBuilder.baseUrl(store.getBaseUrl()).build();
        restClient.post()
                .uri("/store/stock")
                .contentType(APPLICATION_JSON)
                .body(StoreStockInteraction.from(storeStock))
                .retrieve()
                .toEntity(StoreStockInteraction.class);
    }

    private record StoreStockInteraction(Collection<ProductStock> stock) {
        static StoreStockInteraction from(Collection<StoreStockEntry> storeStock) {
            return new StoreStockInteraction(storeStock.stream()
                    .map(ProductStock::from)
                    .toList()
            );
        }
    }

    private record ProductStock(long productId, String productName, int quantity) {
        static ProductStock from(StoreStockEntry stockEntry) {
            return new ProductStock(stockEntry.getProductId(), stockEntry.getProductName(), stockEntry.getQuantity());
        }
    }
}
