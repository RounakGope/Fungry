package com.fung.fungry.Model;

import com.fung.fungry.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY
            ,cascade=CascadeType.ALL
    )
    private List <OrderItem> orderItems=new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private  User user;

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "address_id",nullable = false)
   private Address address;

    private int expectedTimeInMinutes;
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "restaurant_id",nullable = false)
   private Restaurant restaurant;
    @Enumerated(EnumType.STRING)
            @Column(nullable = false)
    private OrderStatus status;
    @Column(name = "total_amt")
    private Double amount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
