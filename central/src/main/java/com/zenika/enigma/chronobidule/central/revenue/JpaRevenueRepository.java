package com.zenika.enigma.chronobidule.central.revenue;

import org.springframework.data.jpa.repository.JpaRepository;

interface JpaRevenueRepository extends RevenueRepository, JpaRepository<StoreRevenue, Long> {
}
