package com.fls.animecommunity.animesanctuary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.fls.animecommunity.animesanctuary.model.notification.Notification;
import com.fls.animecommunity.animesanctuary.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = Long.parseLong(userDetails.getUsername()); // Assuming the username is the member ID
        return ResponseEntity.ok(notificationService.getUnreadNotifications(memberId));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
