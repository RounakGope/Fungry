package com.fung.fungry.Controller;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ServiceIMPL.OrderServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v2.0/order")
@RequiredArgsConstructor
public class OrderController {

   private final OrderServiceIMPL orderServiceIMPL;

   @PostMapping("/{addressId}")
    public ResponseEntity<OrderDTO> create(
           @PathVariable Long addressId, @AuthenticationPrincipal UserPrincipal principal)
   {
       Long userId=principal.getUser().getUserId();
       OrderDTO orderDTO=orderServiceIMPL.createOrder(userId,addressId);
       return ResponseEntity.ok(orderDTO);
   }
   @DeleteMapping("/{orderId}")
    public ResponseEntity<Void > delete(@PathVariable Long orderId,
                                        @AuthenticationPrincipal UserPrincipal principal)
   {Long userId=principal.getUser().getUserId();
       orderServiceIMPL.removeOrder(orderId,userId);
       return  ResponseEntity.noContent().build();
   }

   @GetMapping("/viewOrderByUser/{orderId}")
    public ResponseEntity<OrderDTO> viewOrder1(@PathVariable Long orderId
   ,@AuthenticationPrincipal UserPrincipal principal)
   {Long userId=principal.getUser().getUserId();
       OrderDTO orderDTO=orderServiceIMPL.viewOrderByIdUser(userId,orderId);
       return ResponseEntity.ok(orderDTO);
   }
    @GetMapping("/viewOrderByRes/{restId}/{orderId}")
    public ResponseEntity<OrderDTO> viewOrder2(@PathVariable Long restId
            ,@PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId=principal.getUser().getUserId();
        OrderDTO orderDTO=orderServiceIMPL.viewOrderByIdRest(userId,restId,orderId);
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/viewAllOrderUser")
    public ResponseEntity<List<OrderDTO>> viewOrder3(
            @AuthenticationPrincipal UserPrincipal principal)
    {Long userId=principal.getUser().getUserId();
        List<OrderDTO> orderDTO=orderServiceIMPL.viewAllOrdersForUser(userId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/viewAllOrderByRest/{restId}")
    public ResponseEntity<List<OrderDTO>> viewOrder4(@PathVariable Long restId,@AuthenticationPrincipal UserPrincipal userPrincipal)

    {
        Long userId=userPrincipal.getUser().getUserId();
        List<OrderDTO> orderDTO=orderServiceIMPL.viewAllOrdersForRest(userId,restId);
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/updateOrderStatus/{orderId}/{restId}")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long orderId , @PathVariable
                                                 Long restId, @RequestParam OrderStatus orderStatus)
    {
        OrderDTO orderDTO=orderServiceIMPL.updateOrderStatus(orderId,restId,orderStatus);
     return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/orderStatus/{orderId}")
    public ResponseEntity<OrderStatus> orderStat(@PathVariable Long orderId,@AuthenticationPrincipal UserPrincipal principal
                                                 )
    {Long userId=principal.getUser().getUserId();
        OrderStatus orderStatus=orderServiceIMPL.getOrderStatus(orderId,userId);
        return ResponseEntity.ok(orderStatus);
    }
    @GetMapping("/orderAmt/{orderId}")
    public ResponseEntity<Long> amount(@PathVariable Long orderId,@AuthenticationPrincipal UserPrincipal principal)
    {Long userId=principal.getUser().getUserId();
        Long amount =orderServiceIMPL.getOrderAmount(orderId,userId);
        return ResponseEntity.ok(amount);
    }

    @PutMapping("/cancelOrder/{orderId}")
    public ResponseEntity<Void > cancel(@PathVariable Long orderId,@AuthenticationPrincipal UserPrincipal principal)
    {Long userId=principal.getUser().getUserId();
        orderServiceIMPL.cancelOrder(orderId,userId);
        return ResponseEntity.noContent().build();
    }








}
