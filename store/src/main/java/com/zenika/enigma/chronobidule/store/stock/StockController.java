package com.zenika.enigma.chronobidule.store.stock;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/store/stock")
class StockController {

    private final StockService service;

    StockController(StockService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    StockInitializationResponse initStock(@RequestBody StockInitializationRequest request) {
        var stock = request.convert();
        service.initStock(stock);
        return StockInitializationResponse.from(stock);
    }

    record StockInitializationRequest(List<ProductStock> stock) {
        List<StockEntry> convert() {
            return stock.stream()
                    .map(ProductStock::convert)
                    .toList();
        }
    }

    record StockInitializationResponse(List<ProductStock> stocks) {
        static StockInitializationResponse from(List<StockEntry> stockEntries) {
            return new StockInitializationResponse(stockEntries.stream().map(ProductStock::from).toList());
        }
    }

    record ProductStock(long productId, String productName, int quantity) {
        static ProductStock from(StockEntry stockEntry) {
            return new ProductStock(stockEntry.getProductId(), stockEntry.getName(), stockEntry.getQuantity());
        }

        StockEntry convert() {
            return new StockEntry(productId, productName, quantity);
        }
    }

}
