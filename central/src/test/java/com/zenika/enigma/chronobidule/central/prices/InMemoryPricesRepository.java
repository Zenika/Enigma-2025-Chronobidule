package com.zenika.enigma.chronobidule.central.prices;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class InMemoryPricesRepository implements PricesRepository {
    private final Map<Long, StoreProductPrice> prices = new HashMap<>();
    private final AtomicLong ids = new AtomicLong();

    @Override
    public Collection<StoreProductPrice> findByStoreId(long storeId) {
        return prices.values().stream()
                .filter(price -> price.getStoreId() == storeId)
                .toList();
    }

    @Override
    public Optional<StoreProductPrice> findByStoreIdAndProductId(long storeId, long productId) {
        return findByStoreId(storeId).stream()
                .filter(entry -> entry.getProductId() == productId)
                .findFirst();
    }

    @Override
    public StoreProductPrice save(StoreProductPrice price) {
        var alreadyExisting = findByStoreIdAndProductId(price.getStoreId(), price.getProductId());
        var priceToSave = price;
        if (alreadyExisting.isPresent()) {
            if (price.getId() != null && !price.getId().equals(alreadyExisting.get().getId())) {
                throw new IllegalStateException("Updating stock entry for store and product with different ID");
            }
            priceToSave = new StoreProductPrice(
                    alreadyExisting.get().getId(),
                    price.getStoreId(),
                    price.getProductId(),
                    price.getAmount()
            );
        } else if (price.getId() == null) {
            priceToSave = new StoreProductPrice(
                    ids.getAndIncrement(),
                    price.getStoreId(),
                    price.getProductId(),
                    price.getAmount()
            );
        }
        prices.put(priceToSave.getId(), priceToSave);
        return priceToSave;
    }
}
