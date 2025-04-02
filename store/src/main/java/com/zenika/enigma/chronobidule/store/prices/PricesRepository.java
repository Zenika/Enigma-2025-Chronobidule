package com.zenika.enigma.chronobidule.store.prices;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricesRepository extends JpaRepository<ProductPrice, Long> {

    Optional<ProductPrice> findByProductId(Long productId);

}
