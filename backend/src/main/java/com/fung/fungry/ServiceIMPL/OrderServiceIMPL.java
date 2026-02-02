package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.Service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceIMPL implements OrderService {

    @Override
    public OrderDTO createOrder(Long cartId, Long userId, Long addressId) {

        return null;

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
