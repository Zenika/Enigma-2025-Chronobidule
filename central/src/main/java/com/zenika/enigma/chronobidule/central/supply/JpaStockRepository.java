package com.zenika.enigma.chronobidule.central.supply;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockRepository extends StockRepository, JpaRepository<StoreStockEntry, Long> {
}
