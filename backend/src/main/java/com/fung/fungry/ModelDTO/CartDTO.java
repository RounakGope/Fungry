package com.fung.fungry.ModelDTO;

import java.util.List;

public class CartDTO {

    private Long cartId;
    private List<CartItemDTO> cartItemDTOS;
    private Double totalAmt;
    private String restaurantName;
}
