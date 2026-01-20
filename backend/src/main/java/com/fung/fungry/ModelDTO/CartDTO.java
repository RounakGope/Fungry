package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CartDTO {

    private Long cartId;
    private List<CartItemDTO> cartItemDTOS;
    private Double totalAmt;
    private String restaurantName;
}
