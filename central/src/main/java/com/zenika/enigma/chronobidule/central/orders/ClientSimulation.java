package com.zenika.enigma.chronobidule.central.orders;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.zenika.enigma.chronobidule.central.prices.PricesInitializer;
import com.zenika.enigma.chronobidule.central.prices.PricesRepository;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoresRepository;
import com.zenika.enigma.chronobidule.central.supply.StockInitializer;
import com.zenika.enigma.chronobidule.central.supply.StockRepository;
import com.zenika.enigma.chronobidule.central.supply.StoreStockEntry;

@Component
public class ClientSimulation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSimulation.class);

    private final StoresRepository storesRepository;
    private final StockRepository stockRepository;
    private final StockInitializer stockInitializer;
    private final PricesRepository pricesRepository;
    private final PricesInitializer pricesInitializer;
    private final OrdersService ordersService;
    private final ApplicationEventPublisher eventPublisher;

    public ClientSimulation(StoresRepository storesRepository, 
    		StockRepository stockRepository, 
    		StockInitializer stockInitializer,
    		PricesRepository pricesRepository, 
    		PricesInitializer pricesInitializer,
    		OrdersService ordersService, 
    		ApplicationEventPublisher eventPublisher) {
        this.storesRepository = storesRepository;
        this.ordersService = ordersService;
        this.eventPublisher = eventPublisher;
        this.stockRepository = stockRepository;
        this.stockInitializer = stockInitializer;
        this.pricesRepository = pricesRepository;
        this.pricesInitializer = pricesInitializer;
    }

    @Scheduled(fixedRate = 10, initialDelay = 10, timeUnit = SECONDS)
    public void placeOrders() {
        var stores = storesRepository.findAll();
        LOGGER.info("Sending orders to {}", stores.stream().map(Store::getName).collect(Collectors.joining(",")));
        stores.stream()
                .map(this::placeOrder)
                .flatMap(Optional::stream)
                .map(this::payOrder)
                .flatMap(Optional::stream)
                .forEach(this::decreaseStock);
    }

    private Optional<Order> placeOrder(Store store) {
        try {
            var stock = stockRepository.findByStoreId(store.getId());
            var faker = new Faker();
            var items = stock.stream()
                    .filter(StoreStockEntry::hasStock)
                    .filter(stockEntry -> faker.bool().bool())
                    .map(stockEntry -> new OrderItem(stockEntry.getProductId(), faker.number()
                            .numberBetween(1, Integer.min(10, stockEntry.getQuantity()))))
                    .toList();
            if (items.isEmpty()) {
                LOGGER.warn("No order placed for store {} {]", store.getId(), store.getName());
                return Optional.empty();
            }
            var order = Order.of(store, items);
            var placedOrder = ordersService.placeOrder(order);
            LOGGER.info("Placed order {}", placedOrder);
            return Optional.of(placedOrder);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Order> payOrder(Order order) {
        try {
            var paidOrder = ordersService.payOrder(order);
            LOGGER.info("Paid {} euros for order {} in store {}", paidOrder.getPaymentAmount(), paidOrder.getId(), paidOrder.getStoreId());
            eventPublisher.publishEvent(new OrderPaid(order.getStoreId(), order.getId(), order.getPaymentAmount()));
            return Optional.of(paidOrder);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return Optional.empty();
        }
    }

    private void decreaseStock(Order order) {
        var updatedStock = order.getItems().stream()
                .flatMap(item -> stockRepository.findByStoreIdAndProductId(order.getStoreId(), item.getProductId())
                        .map(stock -> stock.decreaseQuantity(item.getQuantity()))
                        .stream()
                ).toList();
        LOGGER.info("Updated store {} stock to {}", order.getStoreId(), updatedStock);
        stockRepository.saveAll(updatedStock);
    }

}
