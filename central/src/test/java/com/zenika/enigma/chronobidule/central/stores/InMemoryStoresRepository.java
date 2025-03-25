package com.zenika.enigma.chronobidule.central.stores;

import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;

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
