package com.fls.animecommunity.animesanctuary.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fls.animecommunity.animesanctuary.exception.ApplicationException;
import com.fls.animecommunity.animesanctuary.exception.ErrorCode;
import com.fls.animecommunity.animesanctuary.repository.EmitterRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final static Long DEFAULT_TIMEOUT = 3600000L;  // 1시간 타임아웃 설정
    private final static String NOTIFICATION_NAME = "notify";  // 기본 알림 이벤트 이름

    private final EmitterRepository emitterRepository;  // SseEmitter를 저장하고 관리하는 저장소
    
    
    //첫응답
    @Override
    public SseEmitter connectNotification(Long userId) {
        // 새로운 SseEmitter를 생성 (타임아웃 1시간)
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 사용자 ID로 SseEmitter를 저장
        emitterRepository.save(userId, sseEmitter);

        // 연결 종료 또는 타임아웃 시 SseEmitter를 삭제
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        // 초기 연결 시 503 Service Unavailable 오류를 방지하기 위해 첫 데이터를 전송
        try {
            sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("Connection completed"));
        } catch (IOException exception) {
            log.error("Failed to send initial SSE event to user {}: {}", userId, exception.getMessage());
            throw new ApplicationException(ErrorCode.NOTIFICATION_CONNECTION_ERROR);
        }
        
        return sseEmitter;
    }
    
    public void send(Long userId, Long notificationId) {
        // 유저 ID로 SseEmitter를 찾아 이벤트를 발생 시킨다.
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(notificationId.toString()).name(NOTIFICATION_NAME).data("New notification"));
            } catch (IOException exception) {
        // IOException이 발생하면 저장된 SseEmitter를 삭제하고 예외를 발생시킨다.
                emitterRepository.delete(userId);
                throw new ApplicationException(ErrorCode.NOTIFICATION_CONNECTION_ERROR);
            }
        }, () -> log.info("No emitter found"));
    }
}
