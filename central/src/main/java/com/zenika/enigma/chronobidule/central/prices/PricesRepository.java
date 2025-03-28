package com.zenika.enigma.chronobidule.central.prices;

import java.util.Collection;
import java.util.Optional;

public interface PricesRepository {

    Collection<StoreProductPrice> findByStoreId(long storeId);

    Optional<StoreProductPrice> findByStoreIdAndProductId(long storeId, long productId);

    StoreProductPrice save(StoreProductPrice price);

}
