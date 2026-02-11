package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.*;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.OrderItemDTO;
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

    @Autowired
    RestaurantRepository restaurantRepository;
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
        order.setUser(userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such user")));
        order.setExpectedTimeInMinutes(10);
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return mapToOrderDTO(order);
    }

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
    @Transactional
    public void removeOrder(Long orderId, Long userId) {// just removing the order entry from this user order history
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such User Found"));
        Order order=orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("No such order found "));
        orderRepository.delete(order);

    }

    @Override
    public OrderDTO viewOrderByIdUser(Long userId, Long orderID) {
        User user= userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such User Found"));
        Order order=orderRepository.findById(orderID).orElseThrow(()->new RuntimeException("No such Order Found"));
        if (!order.getUser().equals(user))
            throw new RuntimeException("Order User Mismatch");

        return mapToOrderDTO(order);
    }

    @Override
    public OrderDTO viewOrderByIdRest(Long restId, Long orderId) {
        Restaurant restaurant=restaurantRepository.findById(restId).orElseThrow(()->new RuntimeException("No such restaturant present"));
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("No such Order present"));
        if (!order.getRestaurant().equals(restaurant))
            throw new RuntimeException("Order restaurant mismatch");

        return mapToOrderDTO(order);
    }

    @Override
    @Transactional
    public List<OrderDTO> viewAllOrdersForUser(Long userId) {
        User user= userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such User Found"));
        List<Order> orders=user.getOrderHistory();
        return orders.stream().map(order -> mapToOrderDTO(order)).toList();


    }

    @Override
    public List<OrderDTO> viewAllOrdersForRest(Long restaurantId) {
        Restaurant restaurant=restaurantRepository.findById(restaurantId).orElseThrow(()->new RuntimeException("No such restaturant present"));
        List<Order> orderList  =restaurant.getOrders();
        return orderList.stream().map(order -> mapToOrderDTO(order)).toList();

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
