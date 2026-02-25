package com.fung.fungry.Controller;

import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.ModelDTO.StripeResponseDTO;
import com.fung.fungry.ServiceIMPL.PaymentServiceIMPL;
import com.fung.fungry.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v1.0/payments")
public class PaymentController {
    @Autowired
    private PaymentServiceIMPL paymentServiceIMPL;

    @PostMapping("/startPayment/{orderId}")
    public ResponseEntity<StripeResponseDTO> checkoutProduct(@PathVariable Long orderID, @RequestBody PaymentMode paymentMode, @AuthenticationPrincipal CustomUserDetails userDetails)
    {

        return ResponseEntity.ok(paymentServiceIMPL.startPayment(orderID,userDetails.getUserId(),paymentMode));
    }
}
