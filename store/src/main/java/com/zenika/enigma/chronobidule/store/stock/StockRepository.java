package com.zenika.enigma.chronobidule.store.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntry, Long> {

    Optional<StockEntry> findByProductId(Long productIds);

}
