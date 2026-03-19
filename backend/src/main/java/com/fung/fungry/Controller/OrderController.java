package com.fung.fungry.Controller;

import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ServiceIMPL.OrderServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
