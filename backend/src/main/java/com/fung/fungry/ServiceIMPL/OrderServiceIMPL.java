package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.*;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.Repository.*;
import com.fung.fungry.Service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceIMPL implements OrderService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    AddressRepository addressRepository;
    public List<OrderItem> cartToOrderItem(List<CartItem>cartItems,Order order)
    {
        List<OrderItem> orderItems=new ArrayList<>();
        for (CartItem cartItem:cartItems)
        {
            OrderItem orderItem=new OrderItem();
            orderItem.setOrderItemName(cartItem.getMenuItem().getName());
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setOrder(order);
            orderItem.setPrice(cartItem.getMenuItem().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;
    }
    @Override
    @Transactional
    public OrderDTO createOrder(Long cartId, Long userId, Long addressId) {//when the payment is done or cod is selected the created order will created.
        Order order=new Order();
        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new RuntimeException("No such cart present"));
        List<CartItem > cartItems=cart.getCartItems();
        order.setOrderItems(cartToOrderItem(cartItems,order));
        order.setAddress(addressRepository.findById(addressId).orElseThrow(()->new RuntimeException("No such address")));
        order.setCreatedAt(LocalDateTime.now());
        order.setRestaurant(cart.getRestaurant());
        order.setAmount(cart.getTotalAmt());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setUser(userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such user"));
        order.setExpectedTimeInMinutes(10);
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return mapToOrderDTO(order);
    }

    private OrderDTO mapToOrderDTO(Order order) {
    }

    @Override
    public String removeOrder(Long orderId, Long userId) {
        return "";
    }

    @Override
    public OrderDTO viewOrderByIdUser(Long userId, Long orderID) {
        return null;
    }

    @Override
    public OrderDTO viewOrderByIdRest(Long restId, Long orderId) {
        return null;
    }

    @Override
    public List<OrderDTO> viewAllOrdersForUser(Long userId) {
        return List.of();
    }

    @Override
    public List<OrderDTO> viewAllOrdersForRest(Long restaurantId) {
        return List.of();
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, Long restId, OrderStatus nextStatus) {
        return null;
    }

    @Override
    public String getOrderStatus(Long orderId, Long userId) {
        return "";
    }

    @Override
    public Double getOrderAmount(Long orderId, Long userId) {
        return 0.0;
    }

    @Override
    public String cancelOrder(Long orderId, Long userId) {
        return "";
    }
}
