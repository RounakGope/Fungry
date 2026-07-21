package com.fung.fungry.Repository;

import com.fung.fungry.Model.MenuItem;
import com.fung.fungry.Model.Restaurant;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    Optional<Restaurant> findByOwner_UserId(Long userId);
}
