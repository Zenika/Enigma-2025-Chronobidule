package com.zenika.enigma.chronobidule.central.orders;

import com.zenika.enigma.chronobidule.central.prices.PricesRepository;
import com.zenika.enigma.chronobidule.central.prices.StoreProductPrice;
import com.zenika.enigma.chronobidule.central.supply.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Service
@Transactional
public class OrdersService {

    private final StockRepository stockRepository;
    private final OrdersRepository ordersRepository;
    private final PricesRepository pricesRepository;
    private final StoreOrderFacade storeOrderFacade;

    public OrdersService(StockRepository stockRepository, OrdersRepository ordersRepository, PricesRepository pricesRepository, StoreOrderFacade storeOrderFacade) {
        this.stockRepository = stockRepository;
        this.ordersRepository = ordersRepository;
        this.pricesRepository = pricesRepository;
        this.storeOrderFacade = storeOrderFacade;
    }

    public Order placeOrder(Order order) {
        Assert.notNull(order, "Placed order cannot be null");
        var orderToPlace = ordersRepository.save(order);
        var storeOrder = storeOrderFacade.placeOrder(order);
        validateStock(orderToPlace);
        // TODO compare store order and our order's quantities
        orderToPlace.placed();
        return ordersRepository.save(orderToPlace);
    }

    private void validateStock(Order order) {
        order.getItems()
                .forEach(item -> {
                    var quantity = stockRepository.findByStoreIdAndProductId(order.getStoreId(), item.getProductId())
                            .map(stockEntry -> Integer.min(item.getQuantity(), stockEntry.getQuantity()))
                            .orElse(0);
                    item.setQuantity(quantity);
                });
    }

    public Order payOrder(Order order) {
        Assert.notNull(order, "Paid order cannot be null");
        var paidAmount = storeOrderFacade.payOrder(order);
        var paymentAmount = calculatePaymentAmount(order);
        // TODO compare store paid amount and our payment amount
        order.paid(paymentAmount);
        return ordersRepository.save(order);
    }

    private BigDecimal calculatePaymentAmount(Order order) {
        return order.getItems().stream()
                .map(item -> pricesRepository.findByStoreIdAndProductId(order.getStoreId(), item.getProductId())
                        .map(StoreProductPrice::getAmount)
                        .map(amount -> amount.multiply(BigDecimal.valueOf(item.getQuantity())))
                        .orElse(BigDecimal.ZERO)
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
