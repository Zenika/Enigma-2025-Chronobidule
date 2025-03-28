package com.zenika.enigma.chronobidule.central.prices;

import com.zenika.enigma.chronobidule.central.stores.Store;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "store_prices")
public class StoreProductPrice {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private long storeId;
    private long productId;
    private BigDecimal amount;

    public static StoreProductPrice of(Store store, long productId, BigDecimal amount) {
        return new StoreProductPrice(null, store.getId(), productId, amount);
    }

    private StoreProductPrice() {
    }

    public StoreProductPrice(Long id, long storeId, long productId, BigDecimal amount) {
        this.id = id;
        this.storeId = storeId;
        this.productId = productId;
        this.amount = amount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoreProductPrice that = (StoreProductPrice) o;
        return storeId == that.storeId && productId == that.productId && Objects.equals(id, that.id) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeId, productId, amount);
    }

    @Override
    public String toString() {
        return "StoreProductPrice{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", productId=" + productId +
                ", amount=" + amount +
                '}';
    }
}
