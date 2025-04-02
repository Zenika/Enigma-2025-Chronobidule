package com.zenika.enigma.chronobidule.central.stores;

import java.util.Collection;
import java.util.Optional;

public interface StoresRepository {

    Collection<Store> findAll();

    Optional<Store> findById(long id);

    Optional<Store> findByName(String name);

    <S extends Store> Store save(S store);
}
