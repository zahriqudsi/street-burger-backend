package com.streetburger.model;

import java.time.LocalDateTime;

import com.streetburger.model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    public enum NotificationType {
        PROMOTION, REWARD, SYSTEM, RESERVATION, GENERAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser; // null means global notification

    @Column(name = "is_global")
    private Boolean isGlobal = true;

    @Column(name = "notification_type", length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType = NotificationType.PROMOTION;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
