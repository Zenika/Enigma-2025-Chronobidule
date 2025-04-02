package com.zenika.enigma.chronobidule.store.orders;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/store/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping
    OrderResponse placeOrder(@RequestBody OrderRequest request) {
        return OrderResponse.from(ordersService.placeOrder(request.convert()));
    }

    @PostMapping("/{orderId}/payments")
    OrderPaymentResponse payOrder(@PathVariable("orderId") long orderId) {
        return OrderPaymentResponse.from(ordersService.payOrder(orderId));
    }

    record OrderRequest(long id, List<OrderItemDto> items) {
        public Order convert() {
            return new Order(null, id, items.stream().map(OrderItemDto::convert).toList(), OrderStatus.PLACED);
        }
    }

    record OrderResponse(long id, List<OrderItemDto> items) {
        public static OrderResponse from(Order order) {
            return new OrderResponse(order.getId(), order.getItems().stream().map(OrderItemDto::from).toList());
        }
    }

    record OrderItemDto(long productId, int quantity) {
        public static OrderItemDto from(OrderItem item) {
            return new OrderItemDto(item.getProductId(), item.getQuantity());
        }

        public OrderItem convert() {
            return new OrderItem(productId, quantity);
        }
    }

    record OrderPaymentResponse(BigDecimal totalAmount) {
        public static OrderPaymentResponse from(BigDecimal totalAmount) {
            return new OrderPaymentResponse(totalAmount);
        }
    }

}
