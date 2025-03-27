package com.zenika.enigma.chronobidule.central.supply;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.products.ProductsRepository;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class StockInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockInitializer.class);

    private final StockRepository stockRepository;
    private final ProductsRepository productsRepository;
    private final StoreStockFacade storeStockFacade;

    public StockInitializer(StockRepository stockRepository, ProductsRepository productsRepository, StoreStockFacade storeStockFacade) {
        this.stockRepository = stockRepository;
        this.productsRepository = productsRepository;
        this.storeStockFacade = storeStockFacade;
    }

    @Async
    @TransactionalEventListener
    public void onStoreRegistered(StoreRegistered event) {
        LOGGER.info("Initialize stock for store {}", event.store());
        var faker = new Faker();
        var storeStock = productsRepository.findAll().stream()
                .filter(product -> faker.bool().bool())
                .map(productWithStock -> StoreStockEntry.of(event.store(), productWithStock, faker.number().numberBetween(1, 1000)))
                .map(stockRepository::save)
                .toList();
        storeStockFacade.sendStockToStore(event.store(), storeStock);
    }

}
