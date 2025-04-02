package com.zenika.enigma.chronobidule.central.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@DisplayName("Order should")
class OrderTests {

    @Test
    @DisplayName("change status from TO_PLACED to PLACED")
    void placedFromToPlace() {
        var order = new Order(null, 123L, List.of(), OrderStatus.TO_PLACE);
        order.placed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EXCLUDE, names = "TO_PLACE")
    @DisplayName("refuse changing status to PLACED if current status is not TO_PLACE")
    void placedFromInvalidStatus(OrderStatus status) {
        var order = new Order(null, 123L, List.of(), status);
        assertThrows(IllegalStateException.class, order::placed);
    }

}
