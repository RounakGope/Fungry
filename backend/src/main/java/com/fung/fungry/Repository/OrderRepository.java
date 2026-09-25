package com.fung.fungry.Repository;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findByUser(User user, Pageable pageable);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime cutoff);
    Optional<Order> findByStripePaymentIntentId(String stripePaymentIntentId);
    List<Order> findByRestaurant_RestaurantIdAndStatusIn(Long restaurantId, List<OrderStatus> statuses);
    List<Order> findByRestaurant_RestaurantIdAndStatusInOrderByCreatedAtDesc(
            Long restaurantId, List<OrderStatus> statuses);
}
