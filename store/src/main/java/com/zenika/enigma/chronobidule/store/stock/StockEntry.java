package com.zenika.enigma.chronobidule.store.stock;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "stock")
public class StockEntry {
    @Id
    private long productId;
    private String name;
    private int quantity;

    private StockEntry() {
    }

    public StockEntry(long productId, String name, int quantity) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }

    public long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockEntry that = (StockEntry) o;
        return productId == that.productId && quantity == that.quantity && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, quantity);
    }

    @Override
    public String toString() {
        return "StockEntry{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
