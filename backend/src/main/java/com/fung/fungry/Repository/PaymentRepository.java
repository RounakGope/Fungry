package com.fung.fungry.Repository;

import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    public String findByStripeSessionId();
    public Order findByOrderId();

}
