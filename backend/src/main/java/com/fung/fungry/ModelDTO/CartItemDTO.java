package com.fung.fungry.ModelDTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@ToString
@Getter
public class CartItemDTO {
    private Long cartItemId;
    private Double price;
    private Integer quantity;
    private String itemName;

}
