package com.zenika.enigma.chronobidule.store.stock;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/store/stock")
class StockController {

    private final StockService service;

    StockController(StockService service) {
        this.service = service;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    StockResponse getStock() {
        var stock = service.getStock();
        return StockResponse.from(stock);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    StockResponse initStock(@RequestBody StockInitializationRequest request) {
        var stock = request.convert();
        service.initStock(stock);
        return StockResponse.from(stock);
    }

    record StockInitializationRequest(List<ProductStock> stock) {
        List<StockEntry> convert() {
            return stock.stream()
                    .map(ProductStock::convert)
                    .toList();
        }
    }

    record StockResponse(List<ProductStock> stocks) {
        static StockResponse from(List<StockEntry> stockEntries) {
            return new StockResponse(stockEntries.stream().map(ProductStock::from).toList());
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
