package com.zenika.enigma.chronobidule.central.products;

import java.util.Collection;
import java.util.Optional;

public interface ProductsRepository {
    Collection<Product> findAll();

    Optional<Product> findById(Long id);

    <P extends Product> Product save(P product);
}
