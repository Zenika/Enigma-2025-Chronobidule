package com.zenika.enigma.chronobidule.central.orders;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryOrdersRepository implements OrdersRepository {
    private final Map<Long, Order> orders = new HashMap<>();
    private final AtomicLong ids = new AtomicLong();

    @Override
    public Collection<Order> findAll() {
        return orders.values();
    }

    @Override
    public Optional<Order> findById(long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public Order save(Order order) {
        var orderToSave = order;
        if (order.getId() == null) {
            orderToSave = new Order(ids.getAndIncrement(), order.getStoreId(), order.getItems());
        }
        orders.put(orderToSave.getId(), orderToSave);
        return orderToSave;
    }
}
