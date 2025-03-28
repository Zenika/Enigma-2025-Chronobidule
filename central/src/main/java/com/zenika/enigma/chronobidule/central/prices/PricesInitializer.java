package com.zenika.enigma.chronobidule.central.prices;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import com.zenika.enigma.chronobidule.central.supply.StockInitialized;
import com.zenika.enigma.chronobidule.central.supply.StoreStockEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.Collection;

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.STOCK_INITIALIZED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class PricesInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricesInitializer.class);

    private final PricesRepository repository;
    private final StoresRepository storesRepository;
    private final StorePriceFacade storePriceFacade;
    private final ApplicationEventPublisher eventPublisher;

    public PricesInitializer(PricesRepository repository, StoresRepository storesRepository, StorePriceFacade storePriceFacade, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.storesRepository = storesRepository;
        this.storePriceFacade = storePriceFacade;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onStockInitialized(StockInitialized event) {
        if (!event.store().getStatus().equals(STOCK_INITIALIZED)) {
            LOGGER.info("Ignore prices initialization for store {}", event.store());
        } else {
            LOGGER.info("Initialize prices for store {}", event.store());
            var prices = generatePrices(event);
            storePriceFacade.sendPricesToStore(event.store(), prices);
            storesRepository.save(event.store().pricesInitialized());
        }
        eventPublisher.publishEvent(new PricesInitialized(event.store()));
    }

    private Collection<StoreProductPrice> generatePrices(StockInitialized event) {
        var faker = new Faker();
        return event.stock().stream()
                .map(StoreStockEntry::getProductId)
                .map(product -> StoreProductPrice.of(
                        event.store(),
                        product,
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100))
                ))
                .map(repository::save)
                .toList();
    }

}
