package com.fung.fungry.Controller;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.Payment;
import com.fung.fungry.Repository.PaymentRepository;
import com.fung.fungry.Repository.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v.1/payments")
@RequiredArgsConstructor
public class WebHookController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripe/webhook")
    @Transactional
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid signature");
        }

        String eventType = event.getType();

        switch (eventType) {

            case "checkout.session.completed" -> handleCheckoutSuccess(event);

            case "checkout.session.async_payment_failed" -> handleCheckoutFailure(event);

            default -> {
            }
        }

        return ResponseEntity.ok("Webhook processed");
    }

    private void handleCheckoutSuccess(Event event) {

        Session session = (Session) event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        String sessionId = session.getId();

        Payment payment = paymentRepository
                .findByStripeSessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 🛡 Idempotency protection
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    private void handleCheckoutFailure(Event event) {

        Session session = (Session) event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        String sessionId = session.getId();

        Payment payment = paymentRepository
                .findByStripeSessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 🛡 Prevent duplicate failure handling
        if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CREATED);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
