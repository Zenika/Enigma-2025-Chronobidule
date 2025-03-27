package com.zenika.enigma.chronobidule.central.supply;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.zenika.enigma.chronobidule.central.products.Product;
import com.zenika.enigma.chronobidule.central.stores.Store;
import jakarta.persistence.*;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "store_stock_entries")
@JsonAutoDetect(fieldVisibility = ANY)
public class StoreStockEntry {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private long storeId;
    private long productId;
    @Transient
    private String productName;
    private int quantity;

    public static StoreStockEntry of(Store store, Product product, int quantity) {
        return new StoreStockEntry(null, store.getId(), product.getId(), product.getName(), quantity);
    }

    private StoreStockEntry() {
    }

    public StoreStockEntry(Long id, long storeId, long productId, int quantity) {
        this.id = id;
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
    }

    private StoreStockEntry(Long id, long storeId, long productId, String productName, int quantity) {
        this.id = id;
        this.storeId = storeId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public long getStoreId() {
        return storeId;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoreStockEntry that = (StoreStockEntry) o;
        return storeId == that.storeId && productId == that.productId && quantity == that.quantity && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeId, productId, quantity);
    }

    @Override
    public String toString() {
        return "StoreStockEntry{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
