package com.fung.fungry.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
    private List<CartItem> cartItems=new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id",nullable = true)
    private Restaurant restaurant;

    @Column(name = "total_amt",nullable = false)
    private Long totalAmt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

}
