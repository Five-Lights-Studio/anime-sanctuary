package com.fls.animecommunity.animesanctuary.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.notification.Notification;
import com.fls.animecommunity.animesanctuary.repository.NotificationRepository;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotification_shouldSaveNotification_whenValidRequestIsGiven() {
        Member member = new Member();
        member.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);

        Notification notification = new Notification(member, comment, "Test message");

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(member, comment, "Test message");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getUnreadNotifications_shouldReturnNotifications_whenUnreadNotificationsExist() {
        Member member = new Member();
        member.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);

        Notification notification = new Notification(member, comment, "Test message");

        when(notificationRepository.findByRecipientIdAndReadStatusFalse(anyLong())).thenReturn(List.of(notification));

        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(1L);

        assertNotNull(unreadNotifications);
        assertEquals(1, unreadNotifications.size());
        assertEquals("Test message", unreadNotifications.get(0).getMessage());
    }

    @Test
    void markAsRead_shouldUpdateNotification_whenValidNotificationIdIsGiven() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setReadStatus(false);

        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertEquals(true, notification.isReadStatus());
        verify(notificationRepository, times(1)).save(notification);
    }
}
