package com.fung.fungry.Repository;

import com.fung.fungry.Model.Rating;
import com.fung.fungry.Model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {

    Optional<Rating> findByRestaurant(Restaurant restaurant);
}
