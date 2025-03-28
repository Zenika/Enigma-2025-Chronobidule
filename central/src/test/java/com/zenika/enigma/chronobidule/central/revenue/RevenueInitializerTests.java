package com.zenika.enigma.chronobidule.central.revenue;

import com.zenika.enigma.chronobidule.central.prices.PricesInitialized;
import com.zenika.enigma.chronobidule.central.stores.Store;
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

import java.math.BigDecimal;

import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.READY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Revenue initializer should")
class RevenueInitializerTests {

    private RevenueRepository revenueRepository;
    @Mock
    private StoresRepository storesRepository;
    private RevenueInitializer revenueInitializer;

    @Captor
    private ArgumentCaptor<Store> storeCaptor;

    @BeforeEach
    void setUp() {
        revenueRepository = new InMemoryRevenueRepository();
        revenueInitializer = new RevenueInitializer(revenueRepository, storesRepository);
    }

    @Test
    @DisplayName("initialize revenue for a store after registration if status is PRICES_INITIALIZED")
    void initRevenueForStore() {
        var store = new Store(123L, "test store", "http://host/test", StoreStatus.PRICES_INITIALIZED);

        assumeThat(revenueRepository.findByStoreId(123L)).isEmpty();
        revenueInitializer.onPricesInitialized(new PricesInitialized(store));
        assertThat(revenueRepository.findByStoreId(123L)).isPresent().contains(new StoreRevenue(123L, BigDecimal.ZERO));

        verify(storesRepository).save(storeCaptor.capture());
        assertThat(storeCaptor.getValue().getStatus()).isEqualTo(READY);
    }

    @ParameterizedTest
    @EnumSource(value = StoreStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "PRICES_INITIALIZED")
    @DisplayName("ignore revenue initialization for store if status is not PRICES_INITIALIZED")
    void ignoreRevenueInitialization(StoreStatus status) {
        var store = new Store(123L, "test store", "http://host/test", status);

        assumeThat(revenueRepository.findByStoreId(123L)).isEmpty();
        revenueInitializer.onPricesInitialized(new PricesInitialized(store));
        assertThat(revenueRepository.findByStoreId(123L)).isEmpty();

        verifyNoInteractions(storesRepository);
    }

}
