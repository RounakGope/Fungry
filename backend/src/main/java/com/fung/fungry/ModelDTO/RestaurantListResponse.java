package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantListResponse {
    private final List<RestaurantDTO> restaurants;
}
