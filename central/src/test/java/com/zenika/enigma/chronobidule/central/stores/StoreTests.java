package com.zenika.enigma.chronobidule.central.stores;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Store should")
class StoreTests {

    @Test
    @DisplayName("fail creating store with null name")
    void createStoreNullName() {
        assertThrows(IllegalArgumentException.class, () -> Store.of(null, "http://host/test"));
    }

    @Test
    @DisplayName("fail creating store with null base URL")
    void createStoreNullBaseUrl() {
        assertThrows(IllegalArgumentException.class, () -> Store.of("test store", null));
    }

    @Test
    @DisplayName("create a store with status REGISTERED by default")
    void createStoreDefaultRegistered() {
        var actual = Store.of("test store", "http://host/test");
        assertThat(actual.getStatus()).isEqualTo(REGISTERED);
    }

    @Test
    @DisplayName("allow moving to stock initialized from registered status")
    void moveToStockInitialized() {
        var store = Store.of("test store", "http://host/test");
        var actual = store.stockInitialized();
        assertThat(actual).isSameAs(store);
        assertThat(actual.getStatus()).isEqualTo(STOCK_INITIALIZED);
    }

    @ParameterizedTest
    @EnumSource(value = StoreStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "REGISTERED")
    @DisplayName("refuse moving to stock initialized from status different than registered")
    void refuseMovingToStockInitialized(StoreStatus current) {
        var store = new Store(123L, "test store", "http://host/test", current);
        assertThrows(IllegalStateException.class, store::stockInitialized);
    }

    @Test
    @DisplayName("allow moving to prices initialized from stock initialized status")
    void moveToPricesInitialized() {
        var store = new Store(123L, "test store", "http://host/test", STOCK_INITIALIZED);
        var actual = store.pricesInitialized();
        assertThat(actual).isSameAs(store);
        assertThat(actual.getStatus()).isEqualTo(PRICES_INITIALIZED);
    }

    @ParameterizedTest
    @EnumSource(value = StoreStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "STOCK_INITIALIZED")
    @DisplayName("refuse moving to prices initialized from status different than stock initialized")
    void refuseMovingToPricesInitialized(StoreStatus current) {
        var store = new Store(123L, "test store", "http://host/test", current);
        assertThrows(IllegalStateException.class, store::pricesInitialized);
    }

}
