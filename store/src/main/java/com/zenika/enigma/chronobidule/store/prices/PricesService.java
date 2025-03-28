package com.zenika.enigma.chronobidule.store.prices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricesService.class);

    private final PricesRepository repository;

    public PricesService(PricesRepository repository) {
        this.repository = repository;
    }

    public List<ProductPrice> getPrices() {
        return repository.findAll();
    }

    public void initPrices(List<ProductPrice> prices) {
        LOGGER.info("Initializes prices with {}", prices);
        repository.saveAll(prices);
    }

}
