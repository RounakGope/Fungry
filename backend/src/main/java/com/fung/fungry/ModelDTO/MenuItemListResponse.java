package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MenuItemListResponse {
    private final List<MenuItemDTO> menuItems;
}