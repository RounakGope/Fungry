package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.Address;
import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.OrderItem;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.OrderItemDTO;
import com.fung.fungry.ModelDTO.PaymentDTO;
import com.fung.fungry.Repository.OrderRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.PaymentService;
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
    @Override
    public PaymentDTO startPayment(Long orderId, Long userId, PaymentMode paymentMode) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such User Found"));
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("No such order Found"));
        if (!order.getUser().getUserId().equals(userId))
            throw new RuntimeException("Order User Mismatch");
        PaymentMode paymentMode1=paymentMode;
        stripeService.checkoutProduct()
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
