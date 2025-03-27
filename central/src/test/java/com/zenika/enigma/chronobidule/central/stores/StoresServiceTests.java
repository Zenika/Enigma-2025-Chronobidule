package com.zenika.enigma.chronobidule.central.stores;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stores service should")
class StoresServiceTests {

    private StoresRepository repository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    private StoresService service;

    @Captor
    private ArgumentCaptor<StoreRegistered> storeRegisteredCaptor;

    @BeforeEach
    void setUp() {
        repository = new InMemoryStoresRepository();
        service = new StoresService(repository, eventPublisher);
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
            repository.save(new Store(123L, "store 1", "http://host1/api"));
            repository.save(new Store(456L, "store 2", "http://host2/api"));
        }

        @Test
        @DisplayName("return all stores previously saved in repository")
        void withStores() {
            var actual = service.getStores();
            assertThat(actual).containsExactlyInAnyOrder(
                    new Store(123L, "store 1", "http://host1/api"),
                    new Store(456L, "store 2", "http://host2/api")
            );
        }

        @Test
        @DisplayName("fail creating null store")
        void createNullStore() {
            assertThrows(IllegalArgumentException.class, () -> service.createStore(null));
        }

        @Test
        @DisplayName("retrieve store after creation")
        void createStore() {
            var actual = service.createStore(new Store(789L, "new store", "http://host/new_store"));
            SoftAssertions.assertSoftly(s -> {
                s.assertThat(actual).isNotNull();
                s.assertThat(service.getStores()).contains(actual);
            });
            verify(eventPublisher).publishEvent(storeRegisteredCaptor.capture());
            assertThat(storeRegisteredCaptor.getValue().store()).isEqualTo(actual);
        }

        @ParameterizedTest
        @EnumSource(StoreStatus.class)
        @DisplayName("retrieve already existing store, whatever its status")
        void createExistingStore(StoreStatus status) {
            var store = new Store(987L, "existing store", "http://host/existing_store", status);
            repository.save(store);

            var actual = service.createStore(new Store(654L, "existing store", "http://host/existing_store"));
            SoftAssertions.assertSoftly(s -> {
                s.assertThat(actual.getId()).isEqualTo(987L);
                s.assertThat(service.getStores()).contains(store);
            });
            verify(eventPublisher).publishEvent(storeRegisteredCaptor.capture());
            assertThat(storeRegisteredCaptor.getValue().store()).isEqualTo(store);
        }

    }

}
