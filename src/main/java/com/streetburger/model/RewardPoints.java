package com.streetburger.model;

import java.time.LocalDateTime;

import com.streetburger.model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reward_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardPoints {

    public enum TransactionType {
        EARNED, REDEEMED, BONUS, ADMIN_ADD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(length = 255)
    private String description;

    @Column(name = "transaction_type", length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType = TransactionType.EARNED;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
