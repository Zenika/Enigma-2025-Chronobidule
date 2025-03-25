package com.zenika.enigma.chronobidule.stores;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StoresController.class)
@DisplayName("Stores controller should")
class StoresControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private StoresService service;

    @Test
    @DisplayName("return empty list of stores when none exists")
    void emptyStoresList() throws Exception {
        mvc.perform(get("/central/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("stores").isEmpty());
    }

    @Test
    @DisplayName("return list of stores with existing stores")
    void storesListWithExisting() throws Exception {
        when(service.getStores()).thenReturn(List.of(
                new Store(123L, "store 1"),
                new Store(456L, "store 2")
        ));

        mvc.perform(get("/central/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("stores").isNotEmpty())
                .andExpect(jsonPath("stores[0].id", equalTo(123)))
                .andExpect(jsonPath("stores[0].name", equalTo("store 1")))
                .andExpect(jsonPath("stores[1].id", equalTo(456)))
                .andExpect(jsonPath("stores[1].name", equalTo("store 2")));
    }

    @Test
    @DisplayName("save and return created store")
    void createStore() throws Exception {
        when(service.createStore(any())).thenReturn(
                new Store(789L, "new store")
        );

        mvc.perform(post("/central/stores")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name": "new store"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", equalTo(789)))
                .andExpect(jsonPath("name", equalTo("new store")));

        verify(service).createStore(eq(new Store(null, "new store")));
    }

}
