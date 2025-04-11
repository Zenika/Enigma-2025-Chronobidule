package com.zenika.enigma.chronobidule.central.supply;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.products.ProductsRepository;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collection;
import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class StockInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockInitializer.class);

    private final StockRepository stockRepository;
    private final ProductsRepository productsRepository;
    private final StoresRepository storesRepository;
    private final StoreStockFacade storeStockFacade;
    private final ApplicationEventPublisher eventPublisher;

    public StockInitializer(StockRepository stockRepository,
                            ProductsRepository productsRepository,
                            StoresRepository storesRepository,
                            StoreStockFacade storeStockFacade,
                            ApplicationEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.productsRepository = productsRepository;
        this.storesRepository = storesRepository;
        this.storeStockFacade = storeStockFacade;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onStoreRegistered(StoreRegistered event) {
    	Store store = event.store();
        generateStockForStore(store);
    }

	public void generateStockForStore(Store store) {
		Collection<StoreStockEntry> storeStock;
		if (!store.getStatus().equals(StoreStatus.REGISTERED)) {
            LOGGER.info("Ignore stock initialization for store {}", store);
            storeStock = stockRepository.findByStoreId(store.getId());
        } else {
            LOGGER.info("Initialize stock for store {}", store);
            storeStock = generateStock(store);
            storeStockFacade.sendStockToStore(store, storeStock);
            storesRepository.save(store.stockInitialized());
        }
        eventPublisher.publishEvent(new StockInitialized(store, storeStock));
	}

    private Collection<StoreStockEntry> generateStock(Store store) {
        var faker = new Faker();
        List<StoreStockEntry> storeStock = List.of();
        var products = productsRepository.findAll();
        while (storeStock.isEmpty()) {
            storeStock = products.stream()
                    .filter(product -> faker.bool().bool())
                    .map(productWithStock -> StoreStockEntry.of(store, productWithStock, faker.number().numberBetween(1, 1000)))
                    .map(stockRepository::save)
                    .toList();
        }
        return storeStock;
    }

}
