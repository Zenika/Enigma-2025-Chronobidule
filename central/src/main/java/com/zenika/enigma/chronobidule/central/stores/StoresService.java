package com.zenika.enigma.chronobidule.central.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class StoresService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoresService.class);

    private final StoresRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    StoresService(StoresRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public Collection<Store> getStores() {
        return repository.findAll();
    }

    public Store createStore(Store store) {
        Assert.notNull(store, "Cannot create null store");
        var knownStore = repository.findByName(store.getName());
        Store registeredStore;
        if (knownStore.isPresent()) {
            LOGGER.info("Ignoring store {} creation", store.getName());
            registeredStore = knownStore.get();
        } else {
            LOGGER.info("Create store {}, accessible at {}", store.getName(), store.getBaseUrl());
            registeredStore = repository.save(store);
        }
        eventPublisher.publishEvent(new StoreRegistered(registeredStore));
        return registeredStore;
    }

	public Optional<Store> findById(long id) {
		return repository.findById(id);
	}

}
