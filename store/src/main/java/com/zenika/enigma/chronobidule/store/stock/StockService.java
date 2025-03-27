package com.zenika.enigma.chronobidule.store.stock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
class StockService {

    private final StockRepository repository;

    StockService(StockRepository repository) {
        this.repository = repository;
    }

    public void initStock(List<StockEntry> stock) {
        repository.saveAll(stock);
    }

}
