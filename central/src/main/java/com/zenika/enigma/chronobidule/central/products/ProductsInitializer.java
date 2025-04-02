package com.zenika.enigma.chronobidule.central.products;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class ProductsInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsInitializer.class);
    private final ProductsRepository repository;

    public ProductsInitializer(ProductsRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void initializeProducts(ApplicationReadyEvent ready) {
        if (repository.findAll().isEmpty()) {
            var faker = new Faker(new Locale("fr"));
            initializeProducts(() -> faker.beer().name(), 20);
            initializeProducts(() -> faker.food().dish(), 30);
            initializeProducts(() -> faker.food().vegetable(), 30);
            initializeProducts(() -> faker.food().fruit(), 20);
        }
        LOGGER.info("Initialized products: {}", repository.findAll().stream().map(Product::getName).toList());
    }

    private void initializeProducts(Supplier<String> nameSupplier, int numberOfProducts) {
        var productNames = new ArrayList<String>();
        Stream.generate(nameSupplier)
                .filter(productName -> !productNames.contains(productName))
                .limit(numberOfProducts)
                .forEach(productNames::add);

        productNames.stream().map(Product::of).forEach(repository::save);
    }

}
