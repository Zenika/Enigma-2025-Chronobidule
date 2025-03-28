package com.zenika.enigma.chronobidule.store.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
class StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    private final StockRepository repository;

    StockService(StockRepository repository) {
        this.repository = repository;
    }

    public List<StockEntry> getStock() {
        return repository.findAll();
    }

    public void initStock(List<StockEntry> stock) {
        LOGGER.info("Initializes stock with {}", stock);
        repository.saveAll(stock);
    }
}
