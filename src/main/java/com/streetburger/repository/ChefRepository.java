package com.streetburger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.streetburger.model.Chef;

@Repository
public interface ChefRepository extends JpaRepository<Chef, Long> {

    List<Chef> findByIsActiveTrueOrderByDisplayOrderAsc();
}
