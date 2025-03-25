package com.zenika.enigma.chronobidule.central.stores;

import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("JPA stores repository should")
class JpaStoresRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private StoresRepository repository;

    @BeforeEach
    void setUp() {
        entityManager.persist(new Store(null, "store 1"));
        entityManager.persist(new Store(null, "store 2"));
    }

    @Test
    @DisplayName("provide stores saved in database")
    void existingStores() {
        var actual = repository.findAll();
        assertThat(actual).containsExactlyInAnyOrder(
                new Store(1L, "store 1"),
                new Store(2L, "store 2")
        );
    }

    @Test
    @DisplayName("find store after saving it")
    void saveStore() {
        var actual = repository.save(new Store(null, "new store"));
        assertThat(actual.getName()).isEqualTo("new store");
        assertThat(repository.findAll()).contains(actual);
    }

}
