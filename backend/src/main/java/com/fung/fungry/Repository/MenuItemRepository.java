package com.fung.fungry.Repository;

import com.fung.fungry.Model.MenuItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {


   public   List<MenuItem> findByRestaurant_RestaurantId(Long restaurantId, Sort sort);

}
