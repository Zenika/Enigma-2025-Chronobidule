package com.zenika.enigma.chronobidule.central.prices;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPricesRepository extends PricesRepository, JpaRepository<StoreProductPrice, Long> {
}
