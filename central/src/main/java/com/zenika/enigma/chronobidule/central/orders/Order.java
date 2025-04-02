package com.zenika.enigma.chronobidule.central.orders;

import com.zenika.enigma.chronobidule.central.stores.Store;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.zenika.enigma.chronobidule.central.orders.OrderStatus.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private long storeId;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> items;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentDate;

    public static Order of(Store store, List<OrderItem> items) {
        return new Order(null, store.getId(), items, TO_PLACE);
    }

    private Order() {
    }

    public Order(Long id, long storeId, List<OrderItem> items) {
        this(id, storeId, items, TO_PLACE);
    }

    public Order(Long id, long storeId, List<OrderItem> items, OrderStatus status) {
        this.id = id;
        this.storeId = storeId;
        this.items = new ArrayList<>(items);
        this.status = status;
        items.forEach(item -> item.setOrder(this));
    }

    public void placed() {
        if (!status.equals(TO_PLACE)) {
            throw new IllegalStateException("Cannot indicate order as placed with status " + status);
        }
        status = PLACED;
    }

    public void paid(BigDecimal paymentAmount) {
        if (!status.equals(PLACED)) {
            throw new IllegalStateException("Cannot indicate order as paid with status " + status);
        }
        this.status = PAID;
        this.paymentAmount = paymentAmount;
        this.paymentDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public long getStoreId() {
        return storeId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return storeId == order.storeId && Objects.equals(id, order.id) && Objects.equals(items, order.items) && status == order.status && Objects.equals(paymentAmount, order.paymentAmount) && Objects.equals(paymentDate, order.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeId, items, status, paymentAmount, paymentDate);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", items=" + items +
                ", status=" + status +
                ", paymentAmount=" + paymentAmount +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
