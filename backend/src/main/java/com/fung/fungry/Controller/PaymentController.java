package com.fung.fungry.Controller;

import com.fung.fungry.ModelDTO.StripeResponseDTO;
import com.fung.fungry.ServiceIMPL.PaymentServiceIMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-v1.0/payments")
public class PaymentController {
    @Autowired
    private PaymentServiceIMPL paymentServiceIMPL;

    @PostMapping("/")
    public ResponseEntity<StripeResponseDTO> checkoutProduct(@RequestBody )

    {

    }
}
