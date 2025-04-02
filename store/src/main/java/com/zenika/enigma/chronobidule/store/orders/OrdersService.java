package com.zenika.enigma.chronobidule.store.orders;

import com.zenika.enigma.chronobidule.store.prices.PricesRepository;
import com.zenika.enigma.chronobidule.store.prices.ProductPrice;
import com.zenika.enigma.chronobidule.store.stock.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersService.class);

    private final OrdersRepository ordersRepository;
    private final StockRepository stockRepository;
    private final PricesRepository pricesRepository;

    public OrdersService(OrdersRepository ordersRepository, StockRepository stockRepository, PricesRepository pricesRepository) {
        this.ordersRepository = ordersRepository;
        this.stockRepository = stockRepository;
        this.pricesRepository = pricesRepository;
    }

    public Order placeOrder(Order order) {
        LOGGER.info("Place order {}", order);
        var validOrder = validateOrder(order);
        ordersRepository.save(validOrder);
        return validOrder;
    }

    private Order validateOrder(Order order) {
        var items = order.getItems().stream()
                .flatMap(item -> stockRepository.findByProductId(item.getProductId())
                        .map(stock -> Integer.min(stock.getQuantity(), item.getQuantity()))
                        .map(quantity -> new OrderItem(item.getProductId(), quantity))
                        .stream()
                ).toList();
        return new Order(null, order.getCentralId(), items, OrderStatus.PLACED);
    }

    public BigDecimal payOrder(long orderId) {
        LOGGER.info("Pay order {}", orderId);
        var optional = ordersRepository.findByCentralId(orderId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Unknown order " + orderId);
        }
        var order = optional.get();
        if (!order.getStatus().equals(OrderStatus.PLACED)) {
            throw new IllegalStateException("Cannot pay order " + orderId + " with current status " + order.getStatus());
        }
        var totalAmount = calculatePaymentAmount(order);
        order.paid(totalAmount);
        ordersRepository.save(order);
        return totalAmount;
    }

    private BigDecimal calculatePaymentAmount(Order order) {
        return order.getItems().stream()
                .map(item -> pricesRepository.findByProductId(item.getProductId())
                        .map(ProductPrice::getAmount)
                        .map(amount -> amount.multiply(BigDecimal.valueOf(item.getQuantity())))
                        .orElse(BigDecimal.ZERO)
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
