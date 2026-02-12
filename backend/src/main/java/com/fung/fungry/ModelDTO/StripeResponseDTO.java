package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeResponseDTO {
    private PaymentStatus paymentStatus;
    private PaymentStatus status;
    private String message;
    private String sessionId;
    private String sessionURL;
}
