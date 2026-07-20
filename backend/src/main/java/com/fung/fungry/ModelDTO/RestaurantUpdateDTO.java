package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantUpdateDTO {
    private String name;
    private Long id;
    private String description;
    private String cuisine;
    private RestaurantAddressDTO addressDTO;

}
