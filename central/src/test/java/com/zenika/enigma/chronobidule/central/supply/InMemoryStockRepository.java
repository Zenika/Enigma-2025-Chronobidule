package com.zenika.enigma.chronobidule.central.supply;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class InMemoryStockRepository implements StockRepository {
    private final Map<Long, StoreStockEntry> stockEntries = new HashMap<>();
    private final AtomicLong ids = new AtomicLong();

    @Override
    public Collection<StoreStockEntry> findByStoreId(long storeId) {
        return stockEntries.values().stream()
                .filter(entry -> entry.getStoreId() == storeId)
                .toList();
    }

    @Override
    public Optional<StoreStockEntry> findByStoreIdAndProductId(long storeId, long productId) {
        return findByStoreId(storeId).stream()
                .filter(entry -> entry.getProductId() == productId)
                .findFirst();
    }

    @Override
    public StoreStockEntry save(StoreStockEntry stockEntry) {
        var alreadyExisting = findByStoreIdAndProductId(stockEntry.getStoreId(), stockEntry.getProductId());
        var stockEntryToSave = stockEntry;
        if (alreadyExisting.isPresent()) {
            if (stockEntry.getId() != null && !stockEntry.getId().equals(alreadyExisting.get().getId())) {
                throw new IllegalStateException("Updating stock entry for store and product with different ID");
            }
            stockEntryToSave = new StoreStockEntry(
                    alreadyExisting.get().getId(),
                    stockEntry.getStoreId(),
                    stockEntry.getProductId(),
                    stockEntry.getQuantity()
            );
        } else if (stockEntry.getId() == null) {
            stockEntryToSave = new StoreStockEntry(
                    ids.getAndIncrement(),
                    stockEntry.getStoreId(),
                    stockEntry.getProductId(),
                    stockEntry.getQuantity()
            );
        }
        stockEntries.put(stockEntryToSave.getId(), stockEntryToSave);
        return stockEntryToSave;
    }
}
