package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.StripeResponseDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {
    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponseDTO checkoutProduct(OrderDTO orderDTO)
    {
        Stripe.apiKey=secretKey;
        SessionCreateParams.LineItem.PriceData.ProductData productData =SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(orderDTO.getRestaurantName()).build();
        Long amount =Math.round(orderDTO.getTotalAmt()*100);
       SessionCreateParams.LineItem.PriceData priceData =SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("INR")
               .setUnitAmount(amount)
                .build();
        SessionCreateParams.LineItem lineItem=SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();
       SessionCreateParams params= SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .build();
        Session session=null;
                try
                {
                    session=Session.create(params);
                } catch (StripeException e) {
                    throw new RuntimeException(e);
                }
                return StripeResponseDTO.builder()
                        .status(PaymentStatus.SUCCESSFUL)
                        .message("Payment session created")
                        .sessionId(session.getId())
                        .sessionURL(session.getUrl())
                        .build();

    }

}
