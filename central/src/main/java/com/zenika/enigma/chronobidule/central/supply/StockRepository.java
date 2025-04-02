package com.zenika.enigma.chronobidule.central.supply;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockRepository {

    Collection<StoreStockEntry> findByStoreId(long storeId);

    Optional<StoreStockEntry> findByStoreIdAndProductId(long storeId, long productId);

    StoreStockEntry save(StoreStockEntry stockEntry);

    <S extends StoreStockEntry> List<S> saveAll(Iterable<S> stockEntries);
}
