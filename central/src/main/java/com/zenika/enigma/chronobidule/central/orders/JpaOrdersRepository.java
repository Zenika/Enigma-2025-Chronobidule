package com.zenika.enigma.chronobidule.central.orders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrdersRepository extends OrdersRepository, JpaRepository<Order, Long> {
}
