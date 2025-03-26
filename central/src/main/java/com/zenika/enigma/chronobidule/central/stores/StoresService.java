package com.zenika.enigma.chronobidule.central.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;

@Service
@Transactional
public class StoresService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoresService.class);

    private final StoresRepository repository;

    StoresService(StoresRepository repository) {
        this.repository = repository;
    }

    public Collection<Store> getStores() {
        return repository.findAll();
    }

    public Store createStore(Store store) {
        Assert.notNull(store, "Cannot create null store");
        var knownStore = repository.findByName(store.getName());
        if (knownStore.isPresent()) {
            LOGGER.info("Ignoring store {} creation", store.getName());
            return knownStore.get();
        }
        LOGGER.info("Create store {}", store.getName());
        return repository.save(store);
    }

}
