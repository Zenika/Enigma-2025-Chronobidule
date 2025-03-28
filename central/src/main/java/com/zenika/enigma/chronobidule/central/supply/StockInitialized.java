package com.zenika.enigma.chronobidule.central.supply;

import com.zenika.enigma.chronobidule.central.stores.Store;

import java.util.Collection;

public record StockInitialized(Store store, Collection<StoreStockEntry> stock) {
}
