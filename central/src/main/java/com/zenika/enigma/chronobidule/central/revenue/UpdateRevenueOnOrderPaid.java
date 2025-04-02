package com.zenika.enigma.chronobidule.central.revenue;

import com.zenika.enigma.chronobidule.central.orders.OrderPaid;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class UpdateRevenueOnOrderPaid {

    private final RevenueRepository revenueRepository;

    UpdateRevenueOnOrderPaid(RevenueRepository revenueRepository) {
        this.revenueRepository = revenueRepository;
    }

    @EventListener
    void invoke(OrderPaid event) {
        var optionalRevenue = revenueRepository.findByStoreId(event.storeId());
        if (optionalRevenue.isPresent()) {
            var storeRevenue = optionalRevenue.get();
            storeRevenue.increase(event.paymentAmount());
            revenueRepository.save(storeRevenue);
        }
    }

}
