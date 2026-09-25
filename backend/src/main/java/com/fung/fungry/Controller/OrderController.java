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

   @PostMapping("/")
    public ResponseEntity<OrderDTO> create(
           @AuthenticationPrincipal UserPrincipal principal)
   {
       Long userId=principal.getUser().getUserId();
       OrderDTO orderDTO=orderServiceIMPL.createOrder(userId);
       return ResponseEntity.ok(orderDTO);
   }
    @PutMapping("/{orderId}/address/{addressId}")
    public ResponseEntity<OrderDTO> setAddress(@PathVariable Long orderId,
                                               @PathVariable Long addressId,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getUser().getUserId();
        OrderDTO orderDTO = orderServiceIMPL.setOrderAddress(orderId, addressId, userId);
        return ResponseEntity.ok(orderDTO);
    }
    @PostMapping("/confirmCod/{orderId}")
    public ResponseEntity<Void> confirmCod(@PathVariable Long orderId,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getUser().getUserId();
        orderServiceIMPL.confirmCodOrder(orderId, userId);
        return ResponseEntity.noContent().build();
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
    @PutMapping("/updateOrderStatus/{orderId}/{restId}")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long orderId , @PathVariable
                                                 Long restId, @RequestParam OrderStatus orderStatus,
                                                 @AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId = principal.getUser().getUserId();
        OrderDTO orderDTO=orderServiceIMPL.updateOrderStatus(orderId,restId,orderStatus,userId);
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
