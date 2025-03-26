package com.zenika.enigma.chronobidule.central.products;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductsRepository implements ProductsRepository {
    private final Map<Long, Product> products = new HashMap<>();
    private final AtomicLong ids = new AtomicLong();

    @Override
    public Collection<Product> findAll() {
        return products.values();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return findAll().stream()
                .filter(product -> product.getId().equals(id))
                .findAny();
    }

    @Override
    public <P extends Product> Product save(P product) {
        Product productToSave = product;
        if (product.getId() == null) {
            productToSave = new Product(ids.getAndIncrement(), product.getName());
        }
        products.put(productToSave.getId(), productToSave);
        return productToSave;
    }
}
