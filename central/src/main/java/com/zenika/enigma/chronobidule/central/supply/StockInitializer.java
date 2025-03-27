package com.zenika.enigma.chronobidule.central.supply;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.products.ProductsRepository;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class StockInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockInitializer.class);

    private final StockRepository stockRepository;
    private final ProductsRepository productsRepository;
    private final StoresRepository storesRepository;
    private final StoreStockFacade storeStockFacade;

    public StockInitializer(StockRepository stockRepository, ProductsRepository productsRepository, StoresRepository storesRepository, StoreStockFacade storeStockFacade) {
        this.stockRepository = stockRepository;
        this.productsRepository = productsRepository;
        this.storesRepository = storesRepository;
        this.storeStockFacade = storeStockFacade;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onStoreRegistered(StoreRegistered event) {
        if (!event.store().getStatus().equals(StoreStatus.REGISTERED)) {
            LOGGER.info("Ignore stock initialization for store {}", event.store());
            return;
        }
        LOGGER.info("Initialize stock for store {}", event.store());
        var faker = new Faker();
        var storeStock = productsRepository.findAll().stream()
                .filter(product -> faker.bool().bool())
                .map(productWithStock -> StoreStockEntry.of(event.store(), productWithStock, faker.number().numberBetween(1, 1000)))
                .map(stockRepository::save)
                .toList();
        storeStockFacade.sendStockToStore(event.store(), storeStock);
        storesRepository.save(event.store().stockInitialized());
    }

}
