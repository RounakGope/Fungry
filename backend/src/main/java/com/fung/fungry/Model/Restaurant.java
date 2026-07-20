package com.fung.fungry.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "restaurant")
@NoArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    @Column(name = "restaurant_name",nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "cuisine")
    private String cuisine;


    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "restaurant",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Cart> cartList;

    @Embedded
    private RestaurantAddress address;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "restaurant",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MenuItem> menuItems=new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "restaurant",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Order> orders=new ArrayList<>();

    @OneToOne(mappedBy = "restaurant",orphanRemoval = true,cascade = CascadeType.ALL)
    private Rating rating ;


}
