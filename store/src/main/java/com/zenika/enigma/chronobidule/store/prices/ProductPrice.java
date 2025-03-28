package com.zenika.enigma.chronobidule.store.prices;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "prices")
public class ProductPrice {
    @Id
    private long productId;
    private BigDecimal amount;

    private ProductPrice() {
    }

    public ProductPrice(long productId, BigDecimal amount) {
        this.productId = productId;
        this.amount = amount;
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
        ProductPrice that = (ProductPrice) o;
        return productId == that.productId && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, amount);
    }

    @Override
    public String toString() {
        return "ProductPrice{" +
                "productId=" + productId +
                ", amount=" + amount +
                '}';
    }
}
