package com.fung.fungry.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ratings")
@NoArgsConstructor
@Getter
@Setter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long ratingId;
    private Long ratingSum;
    private Long ratingCount;
    private Double ratingAverage;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id",nullable = false,unique = true )
    private Restaurant restaurant;
}
