package com.zenika.enigma.chronobidule.store.orders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByCentralId(long centralId);

}
