package com.zenika.enigma.chronobidule.central.orders;

import java.math.BigDecimal;

public record OrderPaid(long storeId, long orderId, BigDecimal paymentAmount) {
}
