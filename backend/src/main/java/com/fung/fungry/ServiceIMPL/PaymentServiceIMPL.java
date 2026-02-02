package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.ModelDTO.PaymentDTO;
import com.fung.fungry.Service.PaymentService;

import java.util.List;

public class PaymentServiceIMPL  implements PaymentService {
    @Override
    public PaymentDTO startPayment(Long orderId, Long userId, PaymentMode paymentMode) {
        return null;
    }

    @Override
    public PaymentStatus updatePaymentStatus(Long paymentId, PaymentStatus paymentStatus) {
        return null;
    }

    @Override
    public PaymentDTO fetchPayment(Long paymentId) {
        return null;
    }

    @Override
    public List<PaymentDTO> viewPayments(Long userId) {
        return List.of();
    }
}
