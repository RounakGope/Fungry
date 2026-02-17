package com.fung.fungry.Service;


import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.ModelDTO.PaymentDTO;
import com.fung.fungry.ModelDTO.StripeResponseDTO;

import java.util.List;

public interface PaymentService {
    public StripeResponseDTO startPayment(Long orderId, Long userId, PaymentMode paymentMode);
    public PaymentStatus updatePaymentStatus( Long paymentId,PaymentStatus paymentStatus);
    public PaymentDTO fetchPayment(Long paymentId);

    public List<PaymentDTO> viewPayments(Long userId);
    
}
