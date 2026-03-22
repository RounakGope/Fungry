package com.fung.fungry.Repository;

import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    public Optional<Payment> findByStripeSessionId(String stripeSessionId);
    public Order findByOrderId();

    public Optional<Payment> findByPaymentIdAndUser_UserId(Long paymentId,Long userId);
    public List<Payment> findByUser_UserId(Long userId);

}
