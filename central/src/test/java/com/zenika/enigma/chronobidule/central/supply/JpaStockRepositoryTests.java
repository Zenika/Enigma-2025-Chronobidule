package com.zenika.enigma.chronobidule.central.supply;

import com.zenika.enigma.chronobidule.central.products.Product;
import com.zenika.enigma.chronobidule.central.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("JPA stock repository should")
class JpaStockRepositoryTests {

    @Container
    private static PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:17.4-alpine");

    private Store store1;
    private Store store2;
    private Product product1;
    private Product product2;
    private Product product3;

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private StockRepository repository;

    @BeforeEach
    void setUp() {
        store1 = entityManager.persist(new Store(null, "store 1"));
        store2 = entityManager.persist(new Store(null, "store 2"));
        product1 = entityManager.persist(new Product(null, "product 1"));
        product2 = entityManager.persist(new Product(null, "product 2"));
        product3 = entityManager.persist(new Product(null, "product 3"));

        entityManager.persist(new StoreStockEntry(null, store1.getId(), product1.getId(), 10));
        entityManager.persist(new StoreStockEntry(null, store1.getId(), product2.getId(), 20));
        entityManager.persist(new StoreStockEntry(null, store1.getId(), product3.getId(), 30));

        entityManager.persist(new StoreStockEntry(null, store2.getId(), product1.getId(), 1));
        entityManager.persist(new StoreStockEntry(null, store2.getId(), product2.getId(), 2));
    }

    @Test
    @DisplayName("find no stock entry for an unknown store")
    void findByUnknownStore() {
        assertThat(repository.findByStoreId(10000L)).isEmpty();
    }

    @Test
    @DisplayName("find all stock entries for a store")
    void findByStore() {
        var actual = repository.findByStoreId(store1.getId());
        assertThat(actual).usingElementComparator(
                        comparing(StoreStockEntry::getStoreId)
                                .thenComparing(StoreStockEntry::getProductId)
                                .thenComparing(StoreStockEntry::getQuantity)
                )
                .containsExactlyInAnyOrder(
                        new StoreStockEntry(null, store1.getId(), product1.getId(), 10),
                        new StoreStockEntry(null, store1.getId(), product2.getId(), 20),
                        new StoreStockEntry(null, store1.getId(), product3.getId(), 30)
                );
    }

    @Test
    @DisplayName("not allow creating a stock entry for a known product in an unknown store")
    void createStockEntryUnknownStore() {
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(new StoreStockEntry(null, 1000L, product1.getId(), 30)));
    }

    @Test
    @DisplayName("not allow creating a stock entry for an unknown product in a known store")
    void createStockEntryUnknownProduct() {
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(new StoreStockEntry(null, store1.getId(), 1000L, 30)));
    }

    @Test
    @DisplayName("not allow creating a duplicate stock entry for a known product in a known store")
    void createDuplicateStockEntry() {
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(new StoreStockEntry(null, store1.getId(), product1.getId(), 30)));
    }

    @Test
    @DisplayName("create a stock entry for a known product in a known store")
    void createNewStockEntry() {
        var actual = repository.save(new StoreStockEntry(null, store2.getId(), product3.getId(), 30));
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("update an unknown stock entry")
    void updateUnknownStockEntry() {
        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> repository.save(new StoreStockEntry(1000L, store1.getId(), product1.getId(), 30)));
    }

    @Test
    @DisplayName("update a stock entry")
    void updateStockEntry() {
        var stockEntry = repository.findByStoreIdAndProductId(store1.getId(), product1.getId());
        var actual = repository.save(new StoreStockEntry(stockEntry.get().getId(), store1.getId(), product1.getId(), 1000));
        assertThat(actual.getQuantity()).isEqualTo(1000);
    }

    @DynamicPropertySource
    static void dynamicDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DB_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DB_CONTAINER::getPassword);
    }

}
