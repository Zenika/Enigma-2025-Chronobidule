package com.zenika.enigma.chronobidule.central.stores;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "stores")
@JsonAutoDetect(fieldVisibility = ANY)
public class Store {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private URI baseUrl;

    public static Store of(String name, URI baseUrl) {
        Assert.notNull(name, "Invalid store name");
        Assert.notNull(baseUrl, "Invalid store base URL");
        return new Store(null, name, baseUrl);
    }

    private Store() {
    }

    public Store(Long id, String name, URI baseUrl) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public URI getBaseUrl() {
        return baseUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id) && Objects.equals(name, store.name) && Objects.equals(baseUrl, store.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, baseUrl);
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}
