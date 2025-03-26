package com.zenika.enigma.chronobidule.central.supply;

import java.util.Collection;
import java.util.Optional;

public interface StockRepository {

    Collection<StoreStockEntry> findByStoreId(long storeId);

    Optional<StoreStockEntry> findByStoreIdAndProductId(long storeId, long productId);

    StoreStockEntry save(StoreStockEntry stockEntry);

}
