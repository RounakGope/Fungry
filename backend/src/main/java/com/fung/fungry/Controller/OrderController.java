package com.fung.fungry.Controller;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ServiceIMPL.OrderServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v1.0/order")
@RequiredArgsConstructor
public class OrderController {

   private final OrderServiceIMPL orderServiceIMPL;

   @PostMapping("/{cartId}/{userId}/{addressId}")
    public ResponseEntity<OrderDTO> create(@PathVariable Long cartId,
                                           @PathVariable Long userId,
                                           @PathVariable Long addressId)
   {
       OrderDTO orderDTO=orderServiceIMPL.createOrder(cartId,userId,addressId);
       return ResponseEntity.ok(orderDTO);
   }
   @DeleteMapping("/{orderId}/{userId}")
    public ResponseEntity<Void > delete(@PathVariable Long orderId,
                                        @PathVariable Long userId)
   {
       orderServiceIMPL.removeOrder(orderId,userId);
       return  ResponseEntity.noContent().build();
   }

   @GetMapping("/viewOrderByUser/{orderId}/{userId}")
    public ResponseEntity<OrderDTO> viewOrder1(@PathVariable Long orderId
   ,@PathVariable Long userId)
   {
       OrderDTO orderDTO=orderServiceIMPL.viewOrderByIdUser(orderId,userId);
       return ResponseEntity.ok(orderDTO);
   }
    @GetMapping("/viewOrderByRes/{restId}/{orderId}")
    public ResponseEntity<OrderDTO> viewOrder2(@PathVariable Long restId
            ,@PathVariable Long orderId)
    {
        OrderDTO orderDTO=orderServiceIMPL.viewOrderByIdRest(restId,orderId);
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/viewAllOrderUser/{userId}")
    public ResponseEntity<List<OrderDTO>> viewOrder3(
            @PathVariable Long userId)
    {
        List<OrderDTO> orderDTO=orderServiceIMPL.viewAllOrdersForUser(userId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/viewAllOrderByRest/{restId}")
    public ResponseEntity<List<OrderDTO>> viewOrder4(@PathVariable Long restId)
    {
        List<OrderDTO> orderDTO=orderServiceIMPL.viewAllOrdersForRest(restId);
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/updateOrderStatus/{orderId}/{restId}/")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long order , @PathVariable
                                                 Long restId, @RequestParam OrderStatus orderStatus)
    {
        OrderDTO orderDTO=orderServiceIMPL.updateOrderStatus(order,restId,orderStatus);
     return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/orderStatus/{orderId}/{userId}")
    public ResponseEntity<OrderStatus> orderStat(@PathVariable Long orderId,
                                                 @PathVariable Long userId)
    {
        OrderStatus orderStatus=orderServiceIMPL.getOrderStatus(orderId,userId);
        return ResponseEntity.ok(orderStatus);
    }





}
