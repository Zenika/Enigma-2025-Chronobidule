package com.zenika.enigma.chronobidule.central.orders;

import java.util.Collection;
import java.util.Optional;

public interface OrdersRepository {

    Collection<Order> findAll();

    Optional<Order> findById(long orderId);

    Order save(Order order);

}
