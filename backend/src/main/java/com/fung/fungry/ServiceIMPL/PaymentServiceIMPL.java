package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.*;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.Repository.OrderRepository;
import com.fung.fungry.Repository.PaymentRepository;
import com.fung.fungry.Service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@AllArgsConstructor
public class PaymentServiceIMPL  implements PaymentService {
   private final Logger log= LoggerFactory.getLogger(PaymentServiceIMPL.class);

    private final StripeService stripeService;

   private final OrderRepository orderRepository;

   private final PaymentRepository paymentRepository;
    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setAddressDTO(mapToAddressDTO(order.getAddress()));
        orderDTO.setCreatedTime(order.getCreatedAt());
        orderDTO.setOrderItemDTO(mapToOrderItemDTO(order.getOrderItems()));
        orderDTO.setRestaurantName(order.getRestaurant().getName());
        orderDTO.setTotalAmt(order.getAmount());
        return orderDTO;
    }
    private AddressDTO mapToAddressDTO(Address address) {
        AddressDTO addressDTO=new AddressDTO();
        addressDTO.setAddress(address.getAddress());
        addressDTO.setZipcode(address.getZipcode());
        addressDTO.setState(address.getState());
        addressDTO.setLandmark(address.getLandmark());
        addressDTO.setHouseNumber(address.getHouseNumber());
        return addressDTO;

    }
    private List<OrderItemDTO> mapToOrderItemDTO(List<OrderItem> orderItems) {
        List<OrderItemDTO> orderItemDTOS=new ArrayList<>();
        OrderItemDTO orderItemDTO= new OrderItemDTO();
        for (OrderItem orderItem: orderItems)
        {
            orderItemDTO.setName(orderItem.getOrderItemName());
            orderItemDTO.setPrice(orderItem.getPrice());
            orderItemDTO.setQuantity(orderItem.getQuantity());
            orderItemDTO.setOrderItemId(orderItem.getOrderItemId());
            orderItemDTOS.add(orderItemDTO);
        }
        return orderItemDTOS;
    }
    @Transactional
    @Override
    public StripeResponseDTO startPayment(Long orderId, Long userId,PaymentMode paymentMode) {
        log.info("started startPayment for userId={}, with orderId={}",userId,orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("No such order Found"));
        if (!order.getUser().getUserId().equals(userId))
            throw new RuntimeException("Order User Mismatch");
        if (order.getStatus()!= OrderStatus.CREATED)
        {
            log.error("Order cannot be initiated for userId={}",userId);
            throw  new RuntimeException("Order cannot be initiated for payment");
        }
        OrderDTO orderDTO=mapToOrderDTO(order);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        Payment payment=new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setOrder(order);
        payment.setPaymentMode(paymentMode);
        com.fung.fungry.ModelDTO.StripeResponseDTO stripeResponseDTO =stripeService.checkoutProduct(orderDTO);
        payment.setStripeSessionId(stripeResponseDTO.getSessionId());
        log.info("Stripe session created: sessionId={}, orderId={}",
                stripeResponseDTO.getSessionId(), orderId);
        paymentRepository.save(payment);
        orderRepository.save(order);
        log.info("saved order for userid={}",userId);
        return stripeResponseDTO;
    }


    @Override
    public PaymentDTO fetchPayment(Long userId,Long paymentId) {
        Payment payment=paymentRepository.findByPaymentIdAndUser_UserId(paymentId,userId).orElseThrow(()->new RuntimeException("Payment Not Found"));

        return mapToPaymentDTO(payment);

    }

    private PaymentDTO mapToPaymentDTO(Payment payment) {
        PaymentDTO paymentDTO=new PaymentDTO();
        paymentDTO.setPaymentId(payment.getPaymentId());
        paymentDTO.setPaymentMode(payment.getPaymentMode());
        paymentDTO.setPaymentStatus(payment.getPaymentStatus());
        return paymentDTO;
    }

    @Override
    public List<PaymentDTO> viewPayments(Long userId) {
        List<Payment> paymentList=paymentRepository.findByUser_UserId(userId);
        return paymentList.stream().map(this::mapToPaymentDTO).toList();
    }
}
