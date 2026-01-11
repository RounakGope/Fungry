package com.fung.fungry.Model;

import com.fung.fungry.Enums.FoodCategory;
import com.fung.fungry.Enums.FoodType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuItemId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory category;
    @Column(name = "dish_name",nullable = false)
    private String name;
    @Column(name = "av_quantity",nullable = false)
    private Integer availableQuantity;
    @Column(name = "is_available",nullable = false)
    private Boolean isAvailable;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodType type;
    @Column(name = "price",nullable = false)
    private Double price;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id",nullable = false)
    private Restaurant restaurant;

}
