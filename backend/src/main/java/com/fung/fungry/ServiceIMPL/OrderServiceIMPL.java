package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Exception.CartOperationException;
import com.fung.fungry.Exception.OrderOperationException;
import com.fung.fungry.Exception.ResourceNotFoundException;
import com.fung.fungry.Exception.UnauthorisedException;
import com.fung.fungry.Model.*;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.OrderItemDTO;
import com.fung.fungry.Repository.*;
import com.fung.fungry.Service.OrderService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceIMPL implements OrderService {
    private final Logger log= LoggerFactory.getLogger(OrderServiceIMPL.class);
    private final
    UserRepository userRepository;
    private final
    OrderRepository orderRepository;
    private final
    CartRepository cartRepository;
    private final
    AddressRepository addressRepository;

    private final
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
    public OrderDTO createOrder(Long cartId, Long userId, Long addressId) {
        log.info("started create order for userid={}",userId);
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("No such user"));
        Order order=new Order();
        Address address=addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("no such address"));

        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("No such cart present"));
        if (!cart.getUser().getUserId().equals(user.getUserId()))
        {
            log.warn("Userid {} cant create order of cartId{}",userId,cartId);
            throw new CartOperationException("Cart User Mismatch");

        }
        if (!address.getUser().getUserId().equals(user.getUserId()))
        {
            log.warn("Userid {} cant create order of cartId{} as address id {} mismatch",userId,cartId,addressId);
            throw new CartOperationException("Cart address Mismatch");

        }
        List<CartItem > cartItems=cart.getCartItems();
        if (cartItems.isEmpty())
        {
            throw new RuntimeException("cart is empty");
        }
        order.setOrderItems(cartToOrderItem(cartItems,order));
        order.setAddress(address);
        order.setCreatedAt(LocalDateTime.now());
        order.setRestaurant(cart.getRestaurant());
        order.setAmount(cart.getTotalAmt());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setUser(user);
        order.setExpectedTimeInMinutes(10);
        orderRepository.save(order);
        log.info("created order entity for user ={} ,with order id={}",userId,order.getOrderId());
        cart.setRestaurant(null);
        cart.setTotalAmt(0L);
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
        addressDTO.setZipCode(address.getZipcode());
        addressDTO.setState(address.getState());
        addressDTO.setLandMark(address.getLandmark());
        addressDTO.setHouseNumber(address.getHouseNumber());
        return addressDTO;

    }

    private List<OrderItemDTO> mapToOrderItemDTO(List<OrderItem> orderItems) {
        List<OrderItemDTO> orderItemDTOS=new ArrayList<>();
        for (OrderItem orderItem: orderItems)
        {

            OrderItemDTO orderItemDTO= new OrderItemDTO();
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
        log.info("removing order {} , from user id={}",orderId,userId);
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("No such User Found"));
        Order order=orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("No such order found "));
        if(!order.getUser().getUserId().equals(user.getUserId()))
        {
            log.warn("cannot remove order {} , from user id={}",orderId,userId);

            throw new CartOperationException("user order mismatch");
        }
        order.setDeleted(true);
        log.info("deleted order ");

    }

    @Override
    public OrderDTO viewOrderByIdUser(Long userId, Long orderID) {
        Order order=orderRepository.findById(orderID).orElseThrow(()->new ResourceNotFoundException("No such Order Found"));
        if (!order.getUser().getUserId().equals(userId))
            throw new OrderOperationException("Order User Mismatch");

        return mapToOrderDTO(order);
    }

    @Override
    public OrderDTO viewOrderByIdRest(Long restId, Long orderId) {
        Restaurant restaurant=restaurantRepository.findById(restId).orElseThrow(()->new ResourceNotFoundException("No such restaturant present"));
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No such Order present"));
        if (!order.getRestaurant().equals(restaurant))
            throw new OrderOperationException("Order restaurant mismatch");

        return mapToOrderDTO(order);
    }

    @Override
    @Transactional
    public List<OrderDTO> viewAllOrdersForUser(Long userId) {
        User user= userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("No such User Found"));
        List<Order> orders=user.getOrderHistory();
        return orders.stream().map(order -> mapToOrderDTO(order)).toList();


    }

    @Override
    public List<OrderDTO> viewAllOrdersForRest(Long restaurantId) {
        Restaurant restaurant=restaurantRepository.findById(restaurantId).orElseThrow(()->new ResourceNotFoundException("No such restaturant present"));
        List<Order> orderList  =restaurant.getOrders();
        return orderList.stream().map(order -> mapToOrderDTO(order)).toList();

    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Long restId, OrderStatus nextStatus) {
        Restaurant restaurant=restaurantRepository.findById(restId).orElseThrow(()->new ResourceNotFoundException("No such restaturant present"));
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No such order Present"));
        if (!order.getRestaurant().equals(restaurant))
        {
            log.warn("cannot update order status for order ={} , with restId={}",orderId,restId);
            throw new UnauthorisedException("You Dont have Access");
        }
        order.setStatus(nextStatus);
        log.info("updated the order status");
        return mapToOrderDTO(order);

    }

    @Override
    public OrderStatus getOrderStatus(Long orderId, Long userId) {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No such order Present"));
        if (!order.getUser().getUserId().equals(userId))
        {
            throw  new OrderOperationException("Order user mismatch");
        }
        return order.getStatus();

    }

    @Override
    public Long getOrderAmount(Long orderId, Long userId) {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No such order Present"));
        if (!order.getUser().getUserId().equals(userId))
        {
            throw  new OrderOperationException("Order user mismatch");
        }
        return order.getAmount();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        log.info("started cancel order for user ={} with order id{}",userId,orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No such order Present"));
        if (!order.getUser().getUserId().equals(userId))
        {
            throw  new OrderOperationException("Order user mismatch");
        }

        if(order.getStatus()==OrderStatus.CREATED)
        {
            order.setStatus(OrderStatus.CANCELED);
            order.setPaymentStatus(PaymentStatus.CANCELED);
        }
        else
        {
            throw  new OrderOperationException("You cannot cancel order already paid");
        }

        log.info("updated the order status for order {}",orderId);


    }
}
