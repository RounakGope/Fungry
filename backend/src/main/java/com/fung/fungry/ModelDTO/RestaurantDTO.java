package com.fung.fungry.ModelDTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RestaurantDTO {
    private Long restaurantId;
    private String name;
    private Double rating;
}
