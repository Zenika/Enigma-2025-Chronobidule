package com.zenika.enigma.chronobidule.central.stores;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryStoresRepository implements StoresRepository {
    private Map<Long, Store> stores = new HashMap<>();

    @Override
    public Collection<Store> findAll() {
        return stores.values();
    }

    @Override
    public Optional<Store> findByName(String name) {
        Assert.notNull(name, "Cannot find store with null name");
        return findAll().stream()
                .filter(store -> store.getName().equals(name))
                .findFirst();
    }

    @Override
    public Store save(Store store) {
        stores.put(store.getId(), store);
        return store;
    }
}
