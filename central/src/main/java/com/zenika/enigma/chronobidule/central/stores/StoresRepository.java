package com.zenika.enigma.chronobidule.central.stores;

import java.util.Collection;

interface StoresRepository {

    Collection<Store> findAll();

    <S extends Store> Store save(S store);

}
