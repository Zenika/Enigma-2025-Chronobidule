package com.zenika.enigma.chronobidule.central.products;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("JPA products repository should")
class JpaProductsRepositoryTests {

    @Container
    private static PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:17.4-alpine");

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ProductsRepository repository;

    @BeforeEach
    void setUp() {
        entityManager.persist(new Product(null, "product 1"));
        entityManager.persist(new Product(null, "product 2"));
    }

    @Test
    @DisplayName("provide products saved in database")
    void existingProducts() {
        var actual = repository.findAll();
        assertThat(actual).containsExactlyInAnyOrder(
                new Product(1L, "product 1"),
                new Product(2L, "product 2")
        );
    }

    @Test
    @DisplayName("not find product for unknown id")
    void unknownProductId() {
        var actual = repository.findById(1000L);
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("find store after saving it")
    void saveStore() {
        var actual = repository.save(new Product(null, "new product"));
        assertThat(actual.getName()).isEqualTo("new product");
        var found = repository.findById(actual.getId());
        assertThat(found).contains(actual);
        assertThat(found.get().getName()).isEqualTo("new product");
    }

    @DynamicPropertySource
    static void dynamicDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DB_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DB_CONTAINER::getPassword);
    }

}
