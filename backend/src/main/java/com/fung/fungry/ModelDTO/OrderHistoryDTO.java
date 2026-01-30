package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString

public class OrderHistoryDTO {
    private Long orderId;
    private String restaurantName;
    private OrderStatus status;
    private Double totalAmt;
    private LocalDateTime createdTime;
}
