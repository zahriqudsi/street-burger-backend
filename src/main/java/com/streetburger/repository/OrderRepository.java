package com.streetburger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.streetburger.model.Order;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    List<Order> findAllByOrderByCreatedAtDesc();
}
