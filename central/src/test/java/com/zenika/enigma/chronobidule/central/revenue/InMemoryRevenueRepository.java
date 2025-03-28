package com.zenika.enigma.chronobidule.central.revenue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRevenueRepository implements RevenueRepository {
    private final Map<Long, StoreRevenue> revenues = new HashMap<>();

    @Override
    public Collection<StoreRevenue> findAll() {
        return revenues.values();
    }

    @Override
    public Optional<StoreRevenue> findByStoreId(Long storeId) {
        return Optional.ofNullable(revenues.get(storeId));
    }

    @Override
    public StoreRevenue save(StoreRevenue revenue) {
        revenues.put(revenue.getStoreId(), revenue);
        return revenue;
    }

}
