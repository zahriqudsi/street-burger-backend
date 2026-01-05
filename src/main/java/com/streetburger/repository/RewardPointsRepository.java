package com.streetburger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.streetburger.model.RewardPoints;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, Long> {

    List<RewardPoints> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM RewardPoints r WHERE r.user.id = ?1")
    Integer getTotalPointsByUserId(Long userId);

    List<RewardPoints> findByUserPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
