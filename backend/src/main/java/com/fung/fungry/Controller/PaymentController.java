package com.fung.fungry.Controller;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Exception.OrderOperationException;
import com.fung.fungry.Exception.ResourceNotFoundException;
import com.fung.fungry.Model.Order;
import com.fung.fungry.Repository.OrderRepository;import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@RestController
@RequestMapping("/api-v2.0/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final OrderRepository orderRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/create-intent/{orderId}")
    public ResponseEntity<Map<String, String>> createIntent(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal principal) throws StripeException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No such order"));

        if (!order.getUser().getUserId().equals(principal.getUser().getUserId())) {
            throw new OrderOperationException("Order user mismatch");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderOperationException("Order is not awaiting payment");
        }
        if (order.getAddress() == null) {
            throw new OrderOperationException("Set delivery address before paying");
        }

        // Reuse an existing PaymentIntent if one was already created for this order
        if (order.getStripePaymentIntentId() != null) {
            PaymentIntent existing = PaymentIntent.retrieve(order.getStripePaymentIntentId());
            if (!"canceled".equals(existing.getStatus()) && !"succeeded".equals(existing.getStatus())) {
                return ResponseEntity.ok(Map.of("clientSecret", existing.getClientSecret()));
            }
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(order.getAmount() * 100)
                .setCurrency("inr")
                .putMetadata("orderId", String.valueOf(order.getOrderId()))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        order.setStripePaymentIntentId(intent.getId());
        order.setPaymentMode(PaymentMode.ONLINE);
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) throws IOException {
        String payload = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                try {
                    handleSuccess(event);
                } catch (Exception e) {
                    log.error("Failed to process payment_intent.succeeded for event {}", event.getId(), e);
                }
            }
            case "payment_intent.payment_failed" -> {
                try {
                    handleFailure(event);
                } catch (Exception e) {
                    log.error("Failed to process payment_intent.payment_failed for event {}", event.getId(), e);
                }
            }
            default -> log.info("Unhandled Stripe event: {}", event.getType());
        }

        return ResponseEntity.ok("received");
    }

    private PaymentIntent deserializePaymentIntent(Event event) {
        return event.getDataObjectDeserializer().getObject()
                .map(obj -> (PaymentIntent) obj)
                .orElseGet(() -> {
                    try {
                        return (PaymentIntent) event.getDataObjectDeserializer().deserializeUnsafe();
                    } catch (EventDataObjectDeserializationException e) {
                        throw new RuntimeException("Failed to deserialize PaymentIntent for event " + event.getId(), e);
                    }
                });
    }

    private void handleSuccess(Event event) {
        PaymentIntent intent = deserializePaymentIntent(event);
        Order order = orderRepository.findByStripePaymentIntentId(intent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No order for this intent"));

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("Order {} already confirmed, ignoring duplicate webhook", order.getOrderId());
            return;
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            log.error("Payment succeeded for order {} but it is in status {} — needs manual refund review",
                    order.getOrderId(), order.getStatus());
            return;
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        orderRepository.save(order);
        log.info("Order {} confirmed via Stripe webhook", order.getOrderId());
    }

    private void handleFailure(Event event) {
        PaymentIntent intent = deserializePaymentIntent(event);
        Order order = orderRepository.findByStripePaymentIntentId(intent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No order for this intent"));

        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
        log.warn("Payment failed for order {}", order.getOrderId());
    }

}