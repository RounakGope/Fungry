package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.FoodCategory;
import com.fung.fungry.Enums.FoodType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MenuItemDTO {
    private FoodCategory foodCategory;
    private FoodType foodType;
    private String foodName;
    private Integer availableQuantity;
    private Long price;
    private Boolean isAvailable;
}
