package com.streetburger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String message;
    private Long targetUserId;
    private Boolean isGlobal;
    private String notificationType;
    private String imageUrl;
}
