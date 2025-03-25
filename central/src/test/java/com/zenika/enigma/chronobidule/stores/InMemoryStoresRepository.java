package com.zenika.enigma.chronobidule.stores;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class InMemoryStoresRepository implements StoresRepository {
    private Map<Long, Store> stores = new HashMap<>();

    @Override
    public Collection<Store> findAll() {
        return stores.values();
    }

    @Override
    public Store save(Store store) {
        stores.put(store.getId(), store);
        return store;
    }
}
