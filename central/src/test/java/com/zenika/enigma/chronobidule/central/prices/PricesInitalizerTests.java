package com.zenika.enigma.chronobidule.central.prices;

import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import com.zenika.enigma.chronobidule.central.supply.StockInitialized;
import com.zenika.enigma.chronobidule.central.supply.StoreStockEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.PRICES_INITIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Prices initializer should")
class PricesInitalizerTests {

    private PricesRepository pricesRepository;
    @Mock
    private StoresRepository storesRepository;
    @Mock
    private StorePriceFacade storePriceFacade;
    private PricesInitializer pricesInitializer;

    @Captor
    private ArgumentCaptor<Store> storeCaptor;

    @BeforeEach
    void setUp() {
        pricesRepository = new InMemoryPricesRepository();
        pricesInitializer = new PricesInitializer(pricesRepository, storesRepository, storePriceFacade);
    }

    @Test
    @DisplayName("initialize prices for store after registration if status is STOCK_INITIALIZED")
    void initPricesForStore() {
        var store = new Store(123L, "test store", "http://host/test", StoreStatus.STOCK_INITIALIZED);
        var stock = List.of(
                new StoreStockEntry(1000L, 123L, 1L, 111),
                new StoreStockEntry(2000L, 123L, 2L, 222)
        );

        assumeThat(pricesRepository.findByStoreId(123L)).isEmpty();
        pricesInitializer.onStockInitialized(new StockInitialized(store, stock));
        assertThat(pricesRepository.findByStoreId(123L)).isNotEmpty().allMatch(
                price -> price.getAmount() != null && price.getAmount().compareTo(BigDecimal.ZERO) > 0
        );
        verify(storePriceFacade).sendPricesToStore(store, pricesRepository.findByStoreId(store.getId()));
        verify(storesRepository).save(storeCaptor.capture());
        assertThat(storeCaptor.getValue().getStatus()).isEqualTo(PRICES_INITIALIZED);
    }

    @ParameterizedTest
    @EnumSource(value = StoreStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "STOCK_INITIALIZED")
    @DisplayName("ignore prices initialization for store if status is not STOCK_INITIALIZED")
    void ignorePricesInitialization(StoreStatus status) {
        var store = new Store(123L, "test store", "http://host/test", status);
        var stock = List.of(
                new StoreStockEntry(1000L, 123L, 1L, 111),
                new StoreStockEntry(2000L, 123L, 2L, 222)
        );

        assumeThat(pricesRepository.findByStoreId(123L)).isEmpty();
        pricesInitializer.onStockInitialized(new StockInitialized(store, stock));
        assertThat(pricesRepository.findByStoreId(123L)).isEmpty();

        verifyNoInteractions(storePriceFacade);
        verifyNoInteractions(storesRepository);
    }

}
