package com.zenika.enigma.chronobidule.central.stores;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.REGISTERED;
import static com.zenika.enigma.chronobidule.central.stores.StoreStatus.STOCK_INITIALIZED;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "stores")
@JsonAutoDetect(fieldVisibility = ANY)
public class Store {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String baseUrl;
    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    public static Store of(String name, String baseUrl) {
        Assert.notNull(name, "Invalid store name");
        Assert.notNull(baseUrl, "Invalid store base URL");
        return new Store(null, name, baseUrl, REGISTERED);
    }

    private Store() {
    }

    public Store(Long id, String name, String baseUrl) {
        this(id, name, baseUrl, REGISTERED);
    }

    public Store(Long id, String name, String baseUrl, StoreStatus status) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.status = status;
    }

    public Store stockInitialized() {
        if (!status.equals(REGISTERED)) {
            throw new IllegalStateException("Cannot move to stock initialized, current status is " + status);
        }
        status = STOCK_INITIALIZED;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public StoreStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id) && Objects.equals(name, store.name) && Objects.equals(baseUrl, store.baseUrl) && status == store.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, baseUrl, status);
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", baseUrl=" + baseUrl +
                ", status=" + status +
                '}';
    }
}
