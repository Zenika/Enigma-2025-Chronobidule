package com.zenika.enigma.chronobidule.central.revenue;

import com.zenika.enigma.chronobidule.central.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("JPA revenue repository should")
class JpaRevenueRepositoryTests {

    @Container
    private static PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:17.4-alpine");

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private RevenueRepository repository;

    private Store store1;
    private Store store2;

    @BeforeEach
    void setUp() {
        store1 = entityManager.persist(new Store(null, "store 1", "http://host1/api"));
        store2 = entityManager.persist(new Store(null, "store 2", "http://host2/api"));

        entityManager.persist(new StoreRevenue(store1.getId(), BigDecimal.valueOf(12.34)));
    }

    @Test
    @DisplayName("provide revenues saved in database")
    void existingRevenues() {
        var actual = repository.findAll();
        assertThat(actual).containsExactlyInAnyOrder(
                new StoreRevenue(store1.getId(), BigDecimal.valueOf(12.34))
        );
    }

    @Test
    @DisplayName("not find revenue for unknown store ID")
    void unknownStoreId() {
        var actual = repository.findByStoreId(1000L);
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("find revenue for known store ID")
    void knownStoreId() {
        var actual = repository.findByStoreId(store1.getId());
        assertThat(actual).isPresent().contains(new StoreRevenue(store1.getId(), BigDecimal.valueOf(12.34)));
    }

    @Test
    @DisplayName("find revenue after creating it")
    void saveNonExistingRevenue() {
        var actual = repository.save(new StoreRevenue(store2.getId(), BigDecimal.valueOf(56.78)));
        assertThat(repository.findByStoreId(store2.getId())).contains(actual);
    }

    @Test
    @DisplayName("find revenue after updating it")
    void saveAlreadyExistingRevenue() {
        var actual = repository.save(new StoreRevenue(store1.getId(), BigDecimal.valueOf(999)));
        assertThat(repository.findByStoreId(store1.getId())).contains(actual);
    }

    @DynamicPropertySource
    static void dynamicDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DB_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DB_CONTAINER::getPassword);
    }

}
