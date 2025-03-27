package com.zenika.enigma.chronobidule.central.stores;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/central/stores")
@Validated
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
    Store createStore(@RequestBody @Valid CreateStoreRequest request) {
        return service.createStore(request.toModel());
    }

    record StoresListResponse(Collection<Store> stores) {
    }

    record CreateStoreRequest(@NotBlank String name, @NotBlank @URL String baseUrl) {
        public Store toModel() {
            return Store.of(name, URI.create(baseUrl));
        }
    }

}
