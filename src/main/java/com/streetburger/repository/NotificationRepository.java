package com.streetburger.repository;

import java.util.List;

import com.streetburger.model.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByIsGlobalTrueOrderByCreatedAtDesc();

    List<Notification> findByTargetUserIdOrIsGlobalTrueOrderByCreatedAtDesc(Long userId);

    List<Notification> findByTargetUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findAllByOrderByCreatedAtDesc();
}
