package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Enums.PaymentMode;
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

import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final MenuItemRepository menuItemRepository;

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
    public OrderDTO createOrder(Long userId) {
        log.info("started create order for userid={}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("No such user"));
        Order order = new Order();
        Long cartId = user.getCart().getCartId();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("No such cart present"));
        if (!cart.getUser().getUserId().equals(user.getUserId())) {
            log.warn("Userid {} cant create order of cartId{}", userId, cartId);
            throw new CartOperationException("Cart User Mismatch");
        }

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new CartOperationException("Cart is empty");
        }

        // Check availability and reserve stock BEFORE committing to the order,
        // so a failure here leaves nothing half-decremented (rolled back by @Transactional).
        long recalculatedAmount = 0L;
        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = cartItem.getMenuItem();
            if (menuItem.getAvailableQuantity() < cartItem.getQuantity()) {
                throw new CartOperationException(
                        "Not enough stock for " + menuItem.getName() + ", only " + menuItem.getAvailableQuantity() + " left");
            }
            recalculatedAmount += menuItem.getPrice() * cartItem.getQuantity();
        }
        for (CartItem cartItem : cartItems) {
            reduceMenuItems(cartItem.getMenuItem().getMenuItemId(), cartItem.getQuantity());
        }

        order.setOrderItems(cartToOrderItem(cartItems, order));
        order.setCreatedAt(LocalDateTime.now());
        order.setRestaurant(cart.getRestaurant());
        order.setAmount(recalculatedAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setUser(user);
        order.setExpectedTimeInMinutes(10);
        orderRepository.save(order);
        log.info("created order entity for user ={} ,with order id={}", userId, order.getOrderId());

        cart.setRestaurant(null);
        cart.setTotalAmt(0L);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return mapToOrderDTO(order);
    }

    // in OrderServiceIMPL
    @Override
    @Transactional
    public void confirmCodOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No such order"));
        if (!order.getUser().getUserId().equals(userId)) {
            throw new OrderOperationException("Order user mismatch");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMode(PaymentMode.CASH);
        orderRepository.save(order); // paymentStatus stays PENDING until cash is collected
    }
    @Override
    @Transactional
    public OrderDTO setOrderAddress(Long orderId, Long addressId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No such order"));
        if (!order.getUser().getUserId().equals(userId)) {
            throw new OrderOperationException("Order user mismatch");
        }
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("No such address"));
        if (!address.getUser().getUserId().equals(userId)) {
            throw new OrderOperationException("Address user mismatch");
        }
        order.setAddress(address);
        orderRepository.save(order);
        return mapToOrderDTO(order);
    }

    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setStatus(order.getStatus());

        orderDTO.setAddressDTO(order.getAddress() != null ? mapToAddressDTO(order.getAddress()) : null);
        orderDTO.setCreatedTime(order.getCreatedAt());
        orderDTO.setOrderItemDTO(mapToOrderItemDTO(order.getOrderItems()));
        orderDTO.setRestaurantName(order.getRestaurant().getName());
        orderDTO.setTotalAmt(order.getAmount());
        orderDTO.setCustomerName(order.getUser().getUserName());
        orderDTO.setCustomerMobile(order.getUser().getPhoneNumber());
        orderDTO.setPaymentMode(order.getPaymentMode());
        orderDTO.setPaymentStatus(order.getPaymentStatus());
        return orderDTO;
    }
    private AddressDTO mapToAddressDTO(Address address) {
        AddressDTO addressDTO=new AddressDTO();
        addressDTO.setAddress(address.getAddress());
        addressDTO.setAddressId(address.getAddressId());
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
    public OrderDTO viewOrderByIdRest(Long userId,Long restId, Long orderId) {
        Restaurant restaurant=restaurantRepository.findById(restId).orElseThrow(()->new ResourceNotFoundException("No such restaturant present"));
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No such Order present"));
        if (!order.getRestaurant().equals(restaurant))
            throw new OrderOperationException("Order restaurant mismatch");
        if (!order.getRestaurant().getOwner().getUserId().equals(userId))
            throw new UnauthorisedException("You are Unauthorised");

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
    public List<OrderDTO> viewAllOrdersForRest(Long userId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("No such restaturant present"));

        if (!restaurant.getOwner().getUserId().equals(userId))
            throw new UnauthorisedException("You are unauthorised");

        List<OrderStatus> visibleStatuses = List.of(
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED,
                OrderStatus.CANCELED
        );

        List<Order> orderList = orderRepository
                .findByRestaurant_RestaurantIdAndStatusInOrderByCreatedAtDesc(restaurantId, visibleStatuses);

        return orderList.stream().map(this::mapToOrderDTO).toList();
    }

    private static final Map<OrderStatus, OrderStatus> ALLOWED_NEXT_STATUS = Map.of(
            OrderStatus.CONFIRMED, OrderStatus.PREPARING,
            OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED
    );

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Long restId, OrderStatus nextStatus, Long userId) {
        Restaurant restaurant = restaurantRepository.findById(restId)
                .orElseThrow(() -> new ResourceNotFoundException("No such restaturant present"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No such order Present"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No such user present"));

        // The caller must own this restaurant (or be an admin); otherwise anyone
        // logged in could advance any order just by changing the IDs in the URL.
        boolean isOwner = restaurant.getOwner() != null
                && restaurant.getOwner().getUserId().equals(userId);
        if (!isOwner && user.getRole() != UserRole.ADMIN) {
            log.warn("user={} tried to update order={} of restaurant={} without access", userId, orderId, restId);
            throw new UnauthorisedException("You Dont have Access");
        }

        if (!order.getRestaurant().getRestaurantId().equals(restaurant.getRestaurantId())) {
            log.warn("cannot update order status for order={}, with restId={}", orderId, restId);
            throw new UnauthorisedException("You Dont have Access");
        }

        OrderStatus currentStatus = order.getStatus();

        // Owner can only ever act on orders that are already CONFIRMED or later
        if (currentStatus == OrderStatus.CREATED) {
            throw new OrderOperationException("Order is not confirmed yet, cannot update status");
        }

        // Terminal states — no further changes allowed
        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELED) {
            throw new OrderOperationException("Order is already " + currentStatus + ", cannot be updated further");
        }

        OrderStatus allowedNext = ALLOWED_NEXT_STATUS.get(currentStatus);
        if (allowedNext == null || !allowedNext.equals(nextStatus)) {
            throw new OrderOperationException(
                    "Invalid status transition from " + currentStatus + " to " + nextStatus);
        }

        order.setStatus(nextStatus);
        log.info("updated the order status from {} to {} for orderId={}", currentStatus, nextStatus, orderId);
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
    public void reduceMenuItems(Long menuItemId,Integer quantity) {
        MenuItem item=menuItemRepository.findById(menuItemId).orElseThrow(()->new ResourceNotFoundException("No such MenuItem Present"));
        item.setAvailableQuantity(item.getAvailableQuantity()-quantity);
        menuItemRepository.save(item);
    }

    @Override
    public void revertMenuItems(Long menuItemId,Integer quantity) {
        MenuItem item=menuItemRepository.findById(menuItemId).orElseThrow(()->new ResourceNotFoundException("No such MenuItem Present"));
        item.setAvailableQuantity(item.getAvailableQuantity()+quantity);
        menuItemRepository.save(item);

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
        log.info("started cancel order for user ={} with order id{}", userId, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No such order Present"));
        if (!order.getUser().getUserId().equals(userId)) {
            throw new OrderOperationException("Order user mismatch");
        }

        boolean beforePreparation = order.getStatus() == OrderStatus.CREATED
                || order.getStatus() == OrderStatus.CONFIRMED;
        if (!beforePreparation) {
            throw new OrderOperationException("Order can only be cancelled before preparation starts");
        }

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            refundPayment(order.getStripePaymentIntentId());
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        } else {
            order.setPaymentStatus(PaymentStatus.CANCELED);
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            revertMenuItems(orderItem.getMenuItem().getMenuItemId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("updated the order status for order {}", orderId);
    }

    private void refundPayment(String paymentIntentId) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();
            Refund.create(params);
            log.info("Refund issued for payment intent {}", paymentIntentId);
        } catch (StripeException e) {
            log.error("Refund failed for payment intent {}", paymentIntentId, e);
            throw new OrderOperationException("Refund failed, order was not cancelled: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public void expireStaleOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No such order Present"));
        if ( order.getStatus() != OrderStatus.CREATED){
            return;
        }


        for (OrderItem orderItem : order.getOrderItems()) {
            revertMenuItems(orderItem.getMenuItem().getMenuItemId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setPaymentStatus(PaymentStatus.CANCELED);
        orderRepository.save(order);

        log.info("auto-expired stale order {}", orderId);
    }
}
