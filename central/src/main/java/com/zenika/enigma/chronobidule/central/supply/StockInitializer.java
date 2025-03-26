package com.zenika.enigma.chronobidule.central.supply;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.products.ProductsRepository;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import org.springframework.context.event.EventListener;

public class StockInitializer {

    private final StockRepository stockRepository;
    private final ProductsRepository productsRepository;

    public StockInitializer(StockRepository stockRepository, ProductsRepository productsRepository) {
        this.stockRepository = stockRepository;
        this.productsRepository = productsRepository;
    }

    @EventListener
    public void onStoreRegistered(StoreRegistered event) {
        var faker = new Faker();
        productsRepository.findAll().stream()
                .filter(product -> faker.bool().bool())
                .map(productWithStock -> StoreStockEntry.of(event.store(), productWithStock, faker.number().numberBetween(1, 1000)))
                .forEach(stockRepository::save);
    }

}
