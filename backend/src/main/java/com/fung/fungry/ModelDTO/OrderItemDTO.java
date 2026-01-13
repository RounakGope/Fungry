package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderItemDTO {

    private Long orderItemId;
    private String name;
    private Integer quantity;
    private Double price;
}
