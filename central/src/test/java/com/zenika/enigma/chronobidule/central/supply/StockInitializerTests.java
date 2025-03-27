package com.zenika.enigma.chronobidule.central.supply;

import com.zenika.enigma.chronobidule.central.products.InMemoryProductsRepository;
import com.zenika.enigma.chronobidule.central.products.Product;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
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

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.STOCK_INITIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock initializer should")
class StockInitializerTests {

    private StockRepository stockRepository;
    @Mock
    private StoresRepository storesRepository;
    @Mock
    private StoreStockFacade storeStockFacade;
    private StockInitializer stockInitializer;

    @Captor
    private ArgumentCaptor<Store> storeCaptor;

    @BeforeEach
    void setUp() {
        var productsRepository = new InMemoryProductsRepository();
        stockRepository = new InMemoryStockRepository();
        stockInitializer = new StockInitializer(stockRepository, productsRepository, storesRepository, storeStockFacade);

        productsRepository.save(new Product(1L, "product 1"));
        productsRepository.save(new Product(2L, "product 2"));
        productsRepository.save(new Product(3L, "product 3"));
    }

    @Test
    @DisplayName("initialize stock for store after registration if status is REGISTERED")
    void initializeStockForStore() {
        var store = new Store(123L, "test store", "http://host/test", StoreStatus.REGISTERED);
        assumeThat(stockRepository.findByStoreId(123L)).isEmpty();
        stockInitializer.onStoreRegistered(new StoreRegistered(store));
        assertThat(stockRepository.findByStoreId(123L)).isNotEmpty().allMatch(
                stockEntry -> stockEntry.getQuantity() > 0
        );
        verify(storeStockFacade).sendStockToStore(store, stockRepository.findByStoreId(store.getId()));
        verify(storesRepository).save(storeCaptor.capture());
        assertThat(storeCaptor.getValue().getStatus()).isEqualTo(STOCK_INITIALIZED);
    }

    @ParameterizedTest
    @EnumSource(value = StoreStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "REGISTERED")
    @DisplayName("ignore stock initialization for store if status is not REGISTERED")
    void ignoreStockInitialization(StoreStatus status) {
        var store = new Store(123L, "test store", "http://host/test", status);
        assumeThat(stockRepository.findByStoreId(123L)).isEmpty();
        stockInitializer.onStoreRegistered(new StoreRegistered(store));

        verifyNoInteractions(storeStockFacade);
        verifyNoInteractions(storesRepository);
    }

}
