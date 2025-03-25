package com.zenika.enigma.chronobidule.central.stores;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public static Store of(String name) {
        return new Store(null, name);
    }

    private Store() {
    }

    public Store(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id) && Objects.equals(name, store.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
