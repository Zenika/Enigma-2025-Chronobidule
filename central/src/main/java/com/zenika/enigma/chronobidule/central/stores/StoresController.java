package com.zenika.enigma.chronobidule.central.stores;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.zenika.enigma.chronobidule.central.revenue.RevenueInitializer;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/central/stores")
@Validated
public class StoresController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoresController.class);

    private final StoresService service;

    StoresController(StoresService service) {
        this.service = service;
    }

    @GetMapping
    StoresListResponse getStores() {
        var stores = service.getStores();
        return StoresListResponse.from(stores);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    StoreDto createStore(@RequestBody @Valid CreateStoreRequest request) {
        var store = service.createStore(request.toModel());
        LOGGER.info("Added store {}", store);
        return StoreDto.from(store);
    }

    record StoresListResponse(Collection<StoreDto> stores) {
        static StoresListResponse from(Collection<Store> stores) {
            return new StoresListResponse(stores.stream().map(StoreDto::from).toList());
        }
    }

    record CreateStoreRequest(@NotBlank String name, @NotBlank @URL String baseUrl) {
        Store toModel() {
            return Store.of(name, baseUrl);
        }
    }

    record StoreDto(long id, String name) {
        static StoreDto from(Store store) {
            return new StoreDto(store.getId(), store.getName());
        }
    }

}
