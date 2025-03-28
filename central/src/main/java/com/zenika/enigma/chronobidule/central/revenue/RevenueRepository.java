package com.zenika.enigma.chronobidule.central.revenue;

import java.util.Collection;
import java.util.Optional;

public interface RevenueRepository {

    Collection<StoreRevenue> findAll();

    Optional<StoreRevenue> findByStoreId(Long storeId);

    StoreRevenue save(StoreRevenue revenue);

}
