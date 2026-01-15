package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor

public class PaymentDTO {
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private PaymentMode paymentMode;

}
