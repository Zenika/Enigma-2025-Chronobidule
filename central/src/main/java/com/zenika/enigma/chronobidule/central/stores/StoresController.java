package com.zenika.enigma.chronobidule.central.stores;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/central/stores")
public class StoresController {

    private final StoresService service;

    StoresController(StoresService service) {
        this.service = service;
    }

    @GetMapping
    StoresListResponse getStores() {
        var stores = service.getStores();
        return new StoresListResponse(stores);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    Store createStore(@RequestBody CreateStoreRequest request) {
        return service.createStore(request.toModel());
    }

    record StoresListResponse(Collection<Store> stores) {
    }

    record CreateStoreRequest(String name) {
        public Store toModel() {
            return Store.of(name);
        }
    }

}
