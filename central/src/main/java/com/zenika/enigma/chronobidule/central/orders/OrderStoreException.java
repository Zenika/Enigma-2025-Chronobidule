package com.zenika.enigma.chronobidule.central.orders;

public class OrderStoreException extends RuntimeException {

    public OrderStoreException(String action, long storeId, String message, Throwable throwable) {
        super("Unable to " + action + " for store " + storeId + " : " + message + " (caused by " + throwable.getClass().getCanonicalName() + ")", throwable);
    }

}
