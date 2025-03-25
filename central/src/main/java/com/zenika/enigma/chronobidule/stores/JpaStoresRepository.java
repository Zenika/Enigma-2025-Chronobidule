package com.zenika.enigma.chronobidule.stores;

import org.springframework.data.jpa.repository.JpaRepository;

interface JpaStoresRepository extends StoresRepository, JpaRepository<Store, Long> {
}
