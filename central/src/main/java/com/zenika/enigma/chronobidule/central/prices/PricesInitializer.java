package com.zenika.enigma.chronobidule.central.prices;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import com.zenika.enigma.chronobidule.central.supply.StockInitialized;
import com.zenika.enigma.chronobidule.central.supply.StoreStockEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.Collection;

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.STOCK_INITIALIZED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class PricesInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricesInitializer.class);

    private final PricesRepository repository;
    private final StoresRepository storesRepository;
    private final StorePriceFacade storePriceFacade;

    public PricesInitializer(PricesRepository repository, StoresRepository storesRepository, StorePriceFacade storePriceFacade) {
        this.repository = repository;
        this.storesRepository = storesRepository;
        this.storePriceFacade = storePriceFacade;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onStockInitialized(StockInitialized event) {
        Store store = event.store();
        Collection<StoreStockEntry> stock = event.stock();
		generatePricesForStore(store, stock);
    }

	public void generatePricesForStore(Store store, Collection<StoreStockEntry> stock) {
		if (!store.getStatus().equals(STOCK_INITIALIZED)) {
            LOGGER.info("Ignore prices initialization for store {}", store);
        } else {
            LOGGER.info("Initialize prices for store {}", store);
			var prices = generatePrices(store, stock);
            storePriceFacade.sendPricesToStore(store, prices);
        }
	}

    private Collection<StoreProductPrice> generatePrices(Store store, Collection<StoreStockEntry> stock) {
        var faker = new Faker();
		return stock.stream()
                .map(StoreStockEntry::getProductId)
                .map(product -> StoreProductPrice.of(
                        store,
                        product,
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100))
                ))
                .map(repository::save)
                .toList();
    }

}
