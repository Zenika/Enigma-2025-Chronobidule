package com.zenika.enigma.chronobidule.central.orders;

import com.zenika.enigma.chronobidule.central.prices.PricesRepository;
import com.zenika.enigma.chronobidule.central.supply.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Orders service should")
class OrdersServiceTests {

    @Mock
    private StockRepository stockRepository;
    private OrdersRepository ordersRepository;
    @Mock
    private PricesRepository pricesRepository;
    @Mock
    private StoreOrderFacade storeOrderFacade;
    private OrdersService service;

    @BeforeEach
    void setUp() {
        ordersRepository = new InMemoryOrdersRepository();
        service = new OrdersService(stockRepository, ordersRepository, pricesRepository, storeOrderFacade);
    }

    @Test
    @DisplayName("refuse placing a null order")
    void placeNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(null));

        verifyNoInteractions(storeOrderFacade);
    }

    @Test
    @Disabled
    @DisplayName("place order with available quantities for only one product")
    void availableQuantities1Product() {
        var order = new Order(1L, 123L, List.of(new OrderItem(111L, 11)));

        when(storeOrderFacade.placeOrder(order)).thenReturn(order);

        assumeThat(ordersRepository.findAll()).isEmpty();
        var actual = service.placeOrder(order);
        assertThat(actual).isEqualTo(order);
        assertThat(ordersRepository.findAll()).isNotEmpty().contains(order);
    }

    @Test
    @Disabled
    @DisplayName("place order with fewer available quantities for only one product")
    void fewerQuantities1Product() {
        var order = new Order(1L, 123L, List.of(new OrderItem(111L, 11)));
        var expected = new Order(1L, 123L, List.of(new OrderItem(111L, 10)));

        when(storeOrderFacade.placeOrder(order)).thenReturn(expected);

        assumeThat(ordersRepository.findAll()).isEmpty();
        var actual = service.placeOrder(order);
        assertThat(actual).isEqualTo(expected);
        assertThat(ordersRepository.findAll()).isNotEmpty().contains(expected);
    }

    @Test
    @DisplayName("refuse paying a null order")
    void payNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.payOrder(null));

        verifyNoInteractions(storeOrderFacade);
    }

}
