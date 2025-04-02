package com.zenika.enigma.chronobidule.store.orders;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private long centralId;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> items;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;

    private Order() {
    }

    public Order(Long id, long centralId, List<OrderItem> items, OrderStatus status) {
        this.id = id;
        this.centralId = centralId;
        this.items = items;
        this.status = status;
        items.forEach(item -> item.setOrder(this));
    }

    public void paid(BigDecimal totalAmount) {
        this.status = OrderStatus.PAID;
        this.totalAmount = totalAmount;
        this.paymentDate = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public long getCentralId() {
        return centralId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return centralId == order.centralId && Objects.equals(id, order.id) && Objects.equals(items, order.items) && status == order.status && Objects.equals(totalAmount, order.totalAmount) && Objects.equals(paymentDate, order.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, centralId, items, status, totalAmount, paymentDate);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", centralId=" + centralId +
                ", items=" + items +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
