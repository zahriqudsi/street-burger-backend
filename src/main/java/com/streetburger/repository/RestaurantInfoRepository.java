package com.streetburger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.streetburger.model.RestaurantInfo;

@Repository
public interface RestaurantInfoRepository extends JpaRepository<RestaurantInfo, Long> {
}
