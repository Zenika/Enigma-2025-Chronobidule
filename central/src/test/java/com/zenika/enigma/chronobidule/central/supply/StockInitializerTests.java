package com.zenika.enigma.chronobidule.central.supply;

import com.zenika.enigma.chronobidule.central.products.InMemoryProductsRepository;
import com.zenika.enigma.chronobidule.central.products.Product;
import com.zenika.enigma.chronobidule.central.products.ProductsRepository;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreRegistered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock initializer should")
class StockInitializerTests {

    private StockRepository stockRepository;
    private StockInitializer stockInitializer;
    @Mock
    private StoreStockFacade storeStockFacade;

    @BeforeEach
    void setUp() {
        var productsRepository = new InMemoryProductsRepository();
        stockRepository = new InMemoryStockRepository();
        stockInitializer = new StockInitializer(stockRepository, productsRepository, storeStockFacade);

        productsRepository.save(new Product(1L, "product 1"));
        productsRepository.save(new Product(2L, "product 2"));
        productsRepository.save(new Product(3L, "product 3"));
    }

    @Test
    @DisplayName("initialize stock for store after registration")
    void initializeStockForStore() {
        var store = new Store(123L, "test store", URI.create("http://host/test"));
        assumeThat(stockRepository.findByStoreId(123L)).isEmpty();
        stockInitializer.onStoreRegistered(new StoreRegistered(store));
        assertThat(stockRepository.findByStoreId(123L)).isNotEmpty().allMatch(
                stockEntry -> stockEntry.getQuantity() > 0
        );
        verify(storeStockFacade).sendStockToStore(store, stockRepository.findByStoreId(store.getId()));
    }

}
