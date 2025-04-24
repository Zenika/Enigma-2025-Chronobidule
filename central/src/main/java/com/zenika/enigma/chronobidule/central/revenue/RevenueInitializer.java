package com.zenika.enigma.chronobidule.central.revenue;

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.PRICES_INITIALIZED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.zenika.enigma.chronobidule.central.prices.PricesInitialized;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;

@Component
public class RevenueInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevenueInitializer.class);

    private final RevenueRepository revenueRepository;
    private final StoresRepository storesRepository;

    public RevenueInitializer(RevenueRepository revenueRepository, StoresRepository storesRepository) {
        this.revenueRepository = revenueRepository;
        this.storesRepository = storesRepository;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onPricesInitialized(PricesInitialized event) {
        if (!event.store().getStatus().equals(PRICES_INITIALIZED)) {
            LOGGER.info("Ignore revenue initialization for store {}", event.store());
            return;
        }
        LOGGER.info("Initializes revenues for store {}", event.store());
        revenueRepository.save(StoreRevenue.of(event.store()));
        storesRepository.save(event.store().revenueInitialized());
    }

}
