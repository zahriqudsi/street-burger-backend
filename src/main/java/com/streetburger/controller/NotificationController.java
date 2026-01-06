package com.streetburger.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.NotificationRequest;
import com.streetburger.model.Notification;
import com.streetburger.repository.NotificationRepository;
import com.streetburger.repository.UserRepository;
import com.streetburger.service.PushNotificationService;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notification")
@Tag(name = "Notifications", description = "Notification management")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @PostMapping("/add")
    @Operation(summary = "Create a new notification (Admin)")
    public ResponseEntity<ApiResponse<Notification>> addNotification(@RequestBody NotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setIsGlobal(request.getIsGlobal() != null ? request.getIsGlobal() : true);
        notification.setImageUrl(request.getImageUrl());
        notification.setCreatedAt(LocalDateTime.now());

        if (request.getTargetUserId() != null) {
            User targetUser = userRepository.findById(Objects.requireNonNull(request.getTargetUserId())).orElse(null);
            notification.setTargetUser(targetUser);
            notification.setIsGlobal(false);
        }

        if (request.getNotificationType() != null) {
            notification.setNotificationType(
                    Notification.NotificationType.valueOf(request.getNotificationType().toUpperCase()));
        }

        Notification saved = notificationRepository.save(notification);

        // Send push notification
        try {
            if (saved.getIsGlobal()) {
                List<String> tokens = userRepository.findAll().stream()
                        .map(User::getPushToken)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                pushNotificationService.sendPushNotification(tokens, saved.getTitle(), saved.getMessage(), null);
            } else if (saved.getTargetUser() != null && saved.getTargetUser().getPushToken() != null) {
                pushNotificationService.sendPushNotification(
                        Collections.singletonList(saved.getTargetUser().getPushToken()),
                        saved.getTitle(),
                        saved.getMessage(),
                        null);
            }
        } catch (Exception e) {
            System.err.println("Error triggering push notification: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification created", saved));
    }

    @PutMapping("/updateById/{id}")
    @Operation(summary = "Update a notification")
    public ResponseEntity<ApiResponse<Notification>> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationRequest request) {

        return notificationRepository.findById(id)
                .map(existing -> {
                    if (request.getTitle() != null)
                        existing.setTitle(request.getTitle());
                    if (request.getMessage() != null)
                        existing.setMessage(request.getMessage());
                    if (request.getIsGlobal() != null)
                        existing.setIsGlobal(request.getIsGlobal());
                    if (request.getImageUrl() != null)
                        existing.setImageUrl(request.getImageUrl());
                    if (request.getNotificationType() != null) {
                        existing.setNotificationType(
                                Notification.NotificationType.valueOf(request.getNotificationType().toUpperCase()));
                    }

                    notificationRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Notification updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Notification not found")));
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<ApiResponse<Notification>> getById(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notification -> ResponseEntity.ok(ApiResponse.success(notification)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Notification not found")));
    }

    @GetMapping("/get/all")
    @Operation(summary = "Get all notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getAll() {
        List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/user")
    @Operation(summary = "Get notifications for current user")
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            // Return only global notifications for unauthenticated users
            List<Notification> notifications = notificationRepository.findByIsGlobalTrueOrderByCreatedAtDesc();
            return ResponseEntity.ok(ApiResponse.success(notifications));
        }

        List<Notification> notifications = notificationRepository
                .findByTargetUserIdOrIsGlobalTrueOrderByCreatedAtDesc(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @DeleteMapping("/deleteById/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Notification deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Notification not found"));
    }

    @PutMapping("/markRead/{id}")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setIsRead(true);
                    notificationRepository.save(notification);
                    return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Notification not found")));
    }
}
