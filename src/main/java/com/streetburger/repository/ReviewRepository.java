package com.streetburger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.streetburger.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPhoneNumber(String phoneNumber);

    List<Review> findByUserId(Long userId);

    List<Review> findByIsApprovedTrueOrderByCreatedAtDesc();

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findTop10ByIsApprovedTrueOrderByCreatedAtDesc();
}
