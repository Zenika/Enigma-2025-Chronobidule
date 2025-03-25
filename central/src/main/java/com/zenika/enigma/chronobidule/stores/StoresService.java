package com.zenika.enigma.chronobidule.stores;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class StoresService {

    private final StoresRepository repository;

    StoresService(StoresRepository repository) {
        this.repository = repository;
    }

    public Collection<Store> getStores() {
        return repository.findAll();
    }

    public Store createStore(Store store) {
        return repository.save(store);
    }

}
