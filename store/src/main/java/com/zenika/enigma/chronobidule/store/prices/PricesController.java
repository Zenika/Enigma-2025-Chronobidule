package com.zenika.enigma.chronobidule.store.prices;

import com.zenika.enigma.chronobidule.store.stock.StockEntry;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/store/prices")
public class PricesController {

    private final PricesService service;

    public PricesController(PricesService service) {
        this.service = service;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    PricesResponse getPrices() {
        var prices = service.getPrices();
        return PricesResponse.from(prices);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    PricesResponse initStock(@RequestBody PricesInitializationRequest request) {
        var prices = request.convert();
        service.initPrices(prices);
        return PricesResponse.from(prices);
    }

    record PricesInitializationRequest(List<PriceDto> prices) {
        List<ProductPrice> convert() {
            return prices.stream()
                    .map(PriceDto::convert)
                    .toList();
        }
    }

    record PricesResponse(List<PriceDto> prices) {
        static PricesResponse from(Collection<ProductPrice> prices) {
            return new PricesResponse(prices.stream().map(PriceDto::from).toList());
        }
    }

    record PriceDto(long productId, BigDecimal amount) {
        static PriceDto from(ProductPrice price) {
            return new PriceDto(price.getProductId(), price.getAmount());
        }

        ProductPrice convert() {
            return new ProductPrice(productId, amount);
        }
    }

}
