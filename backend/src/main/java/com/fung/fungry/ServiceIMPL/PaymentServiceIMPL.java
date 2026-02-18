package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.*;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.Repository.OrderRepository;
import com.fung.fungry.Repository.PaymentRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PaymentServiceIMPL  implements PaymentService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StripeService stripeService;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;
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
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such User Found"));
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("No such order Found"));
        if (!order.getUser().getUserId().equals(userId))
            throw new RuntimeException("Order User Mismatch");
        if (order.getStatus()!= OrderStatus.CREATED)
        {
            throw  new RuntimeException("Order cannot be initiated for payment");
        }
        OrderDTO orderDTO=mapToOrderDTO(order);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        Payment payment=new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setOrder(order);
        payment.setPaymentMode(paymentMode);
        paymentRepository.save(payment);
        orderRepository.save(order);

        com.fung.fungry.ModelDTO.StripeResponseDTO stripeResponseDTO =stripeService.checkoutProduct(orderDTO);
        return stripeResponseDTO;
    }
    @Override
    public PaymentStatus updatePaymentStatus(Long paymentId, PaymentStatus paymentStatus) {
        return null;
    }

    @Override
    public PaymentDTO fetchPayment(Long paymentId) {
        return null;
    }

    @Override
    public List<PaymentDTO> viewPayments(Long userId) {
        return List.of();
    }
}
