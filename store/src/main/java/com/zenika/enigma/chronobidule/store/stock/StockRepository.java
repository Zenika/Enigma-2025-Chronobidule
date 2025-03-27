package com.zenika.enigma.chronobidule.store.stock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntry, Long> {
}
