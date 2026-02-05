package com.fung.fungry.Service;


import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Model.Address;
import com.fung.fungry.Model.Order;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.Repository.OrderRepository;

import java.util.List;
import java.util.Optional;
//businness logic
public interface OrderService{

    public OrderDTO createOrder(Long cartId, Long userId, Long addressId);

    public void removeOrder(Long orderId,Long userId);

    public OrderDTO viewOrderByIdUser(Long userId,Long orderID);//user wants to see order

    public OrderDTO viewOrderByIdRest(Long restId,Long orderId);

    public List<OrderDTO> viewAllOrdersForUser(Long userId);// user perspective implementation of paging and sorting

    public List<OrderDTO> viewAllOrdersForRest(Long restaurantId);

    public OrderDTO updateOrderStatus(Long orderId, Long restId, OrderStatus nextStatus);//by restaurant can only go forward

    //public Optional<List<Order>> addOrderItem();

    public String getOrderStatus(Long orderId,Long userId);

    public Double getOrderAmount(Long orderId,Long userId);

    public String cancelOrder(Long orderId,Long userId);

    //ADMIN









}
