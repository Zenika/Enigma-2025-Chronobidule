package com.zenika.enigma.chronobidule.central.stores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("JPA stores repository should")
class JpaStoresRepositoryTests {

    @Container
    private static PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:17.4-alpine");

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private StoresRepository repository;

    @BeforeEach
    void setUp() {
        entityManager.persist(new Store(null, "store 1", URI.create("http://host1/api")));
        entityManager.persist(new Store(null, "store 2", URI.create("http://host2/api")));
    }

    @Test
    @DisplayName("provide stores saved in database")
    void existingStores() {
        var actual = repository.findAll();
        assertThat(actual).containsExactlyInAnyOrder(
                new Store(1L, "store 1", URI.create("http://host1/api")),
                new Store(2L, "store 2", URI.create("http://host2/api"))
        );
    }

    @Test
    @DisplayName("not find store for unknown name")
    void unknownStoreName() {
        var actual = repository.findByName("unknown");
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("find store after saving it")
    void saveStore() {
        var actual = repository.save(new Store(null, "new store", URI.create("http://host/new_store")));
        assertThat(actual.getName()).isEqualTo("new store");
        assertThat(repository.findByName("new store")).contains(actual);
    }

    @DynamicPropertySource
    static void dynamicDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DB_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DB_CONTAINER::getPassword);
    }

}
