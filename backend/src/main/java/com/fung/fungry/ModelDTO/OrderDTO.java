package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.OrderStatus;
import com.fung.fungry.Enums.PaymentMode;
import com.fung.fungry.Enums.PaymentStatus;
import com.fung.fungry.Model.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

//first rule of dto is that it does not expose entity,jpa
@Getter
@Setter
@ToString
public class OrderDTO {
    private Long orderId;

    private String customerName;
    private String customerMobile;

    private AddressDTO  addressDTO;
    private List<OrderItemDTO> orderItemDTO;
    private Integer expecetedTimeInMinutes;
    private String restaurantName;
    private OrderStatus status;
    private Long totalAmt;
    private LocalDateTime createdTime;
    private PaymentStatus paymentStatus;
    private PaymentMode paymentMode;
}
