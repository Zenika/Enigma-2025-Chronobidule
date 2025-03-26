package com.zenika.enigma.chronobidule.central.stores;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Stores service should")
class StoresServiceTests {

    private StoresRepository repository;
    private StoresService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryStoresRepository();
        service = new StoresService(repository);
    }

    @Test
    @DisplayName("return no store if none has been created")
    void noStore() {
        var actual = service.getStores();
        assertThat(actual).isEmpty();
    }

    @Nested
    @DisplayName("with stores")
    class WithStoresTests {

        @BeforeEach
        void setUp() {
            repository.save(new Store(123L, "store 1"));
            repository.save(new Store(456L, "store 2"));
        }

        @Test
        @DisplayName("return all stores previously saved in repository")
        void withStores() {
            var actual = service.getStores();
            assertThat(actual).containsExactlyInAnyOrder(
                    new Store(123L, "store 1"),
                    new Store(456L, "store 2")
            );
        }

        @Test
        @DisplayName("fail creating store with null name")
        void createStoreNullName() {
            assertThrows(IllegalArgumentException.class, () -> Store.of(null));
        }

        @Test
        @DisplayName("fail creating null store")
        void createNullStore() {
            assertThrows(IllegalArgumentException.class, () -> service.createStore(null));
        }

        @Test
        @DisplayName("retrieve store after creation")
        void createStore() {
            var actual = service.createStore(new Store(789L, "new store"));
            SoftAssertions.assertSoftly(s -> {
                s.assertThat(actual).isNotNull();
                s.assertThat(service.getStores()).contains(actual);
            });
        }

        @Test
        @DisplayName("retrieve already existing store")
        void createExistingStore() {
            var store = new Store(987L, "existing store");
            repository.save(store);

            var actual = service.createStore(new Store(654L, "existing store"));
            SoftAssertions.assertSoftly(s -> {
                s.assertThat(actual.getId()).isEqualTo(987L);
                s.assertThat(service.getStores()).contains(store);
            });
        }

    }

}
