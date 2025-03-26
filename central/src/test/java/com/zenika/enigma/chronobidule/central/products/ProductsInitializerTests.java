package com.zenika.enigma.chronobidule.central.products;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Products initializer should")
class ProductsInitializerTests {

    private ProductsRepository repository;
    private ProductsInitializer initializer;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductsRepository();
        initializer = new ProductsInitializer(repository);
    }

    @Test
    @DisplayName("create 100 products if none already exists")
    void create100Products() {
        initializer.initializeProducts(null);
        assertThat(repository.findAll()).hasSize(100);
    }

    @Test
    @DisplayName("do nothing if products already exist")
    void doNothingProductExist() {
        var existing = new Product(1L, "existing");
        repository.save(existing);
        initializer.initializeProducts(null);
        assertThat(repository.findAll()).containsExactly(existing);
    }

}
