package com.zenika.enigma.chronobidule.central.products;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductsRepository extends ProductsRepository, JpaRepository<Product, Long> {
}
