package com.fung.fungry.Scheduler;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Model.Order;
import com.fung.fungry.Repository.OrderRepository;
import com.fung.fungry.ServiceIMPL.CartServiceIMPL;
import com.fung.fungry.ServiceIMPL.OrderServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderExpiryScheduler {
    private final OrderRepository orderRepository;
    private final OrderServiceIMPL orderServiceIMPL;
    private static final Logger log= LoggerFactory.getLogger(CartServiceIMPL.class);
    @Value("${order.expiry-minutes}")
    private int expiryMinutes;

    @Scheduled(fixedRate = 60000) // runs every 60 seconds
    public void expireStaleOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        List<Order> staleOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.CREATED, cutoff);
        for (Order order : staleOrders) {
            try {
                orderServiceIMPL.expireStaleOrder(order.getOrderId());
            } catch (Exception e) {
                log.error("Failed to expire order {}", order.getOrderId(), e);
            }
        }
    }
}