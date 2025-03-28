package com.zenika.enigma.chronobidule.central.revenue;

import com.zenika.enigma.chronobidule.central.stores.Store;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "store_revenue")
public class StoreRevenue {
    @Id
    private Long storeId;
    private BigDecimal total;

    public static StoreRevenue of(Store store) {
        return new StoreRevenue(store.getId(), BigDecimal.ZERO);
    }

    private StoreRevenue() {
    }

    public StoreRevenue(Long storeId, BigDecimal total) {
        this.storeId = storeId;
        this.total = total;
    }

    public Long getStoreId() {
        return storeId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoreRevenue that = (StoreRevenue) o;
        return Objects.equals(storeId, that.storeId) && Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, total);
    }

    @Override
    public String toString() {
        return "StoreRevenue{" +
                "storeId=" + storeId +
                ", total=" + total +
                '}';
    }
}
