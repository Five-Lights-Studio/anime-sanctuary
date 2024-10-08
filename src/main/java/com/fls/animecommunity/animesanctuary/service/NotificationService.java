package com.fls.animecommunity.animesanctuary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.notification.Notification;
import com.fls.animecommunity.animesanctuary.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotification(Member recipient, Comment comment, String message) {
        Notification notification = new Notification(recipient, comment, message);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByRecipientIdAndReadStatusFalse(memberId);
    }    

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setReadStatus(true);  // 수정된 부분
        notificationRepository.save(notification);
    }
}
