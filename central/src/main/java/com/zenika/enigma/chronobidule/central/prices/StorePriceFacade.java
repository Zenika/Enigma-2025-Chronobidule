package com.zenika.enigma.chronobidule.central.prices;

import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class StorePriceFacade {
    @Autowired ApplicationEventPublisher eventPublisher;
	@Autowired StoresRepository storesRepository;
    private final RestClient.Builder restClientBuilder;

    public StorePriceFacade(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public void sendPricesToStore(Store store, Collection<StoreProductPrice> prices) {
        var restClient = restClientBuilder.baseUrl(store.getBaseUrl()).build();
        restClient.post()
                .uri("/store/prices")
                .contentType(APPLICATION_JSON)
                .body(StorePricesInteraction.from(prices))
                .retrieve()
                .toEntity(StorePricesInteraction.class);
        if(store.getStatus()!=StoreStatus.PRICES_INITIALIZED) {
        	storesRepository.save(store.pricesInitialized());
        }
        eventPublisher.publishEvent(new PricesInitialized(store));
    }

    private record StorePricesInteraction(Collection<ProductPrice> prices) {
        static StorePricesInteraction from(Collection<StoreProductPrice> storePrices) {
            return new StorePricesInteraction(storePrices.stream()
                    .map(ProductPrice::from)
                    .toList()
            );
        }
    }

    private record ProductPrice(long productId, BigDecimal amount) {
        static ProductPrice from(StoreProductPrice price) {
            return new ProductPrice(price.getProductId(), price.getAmount());
        }
    }
}
