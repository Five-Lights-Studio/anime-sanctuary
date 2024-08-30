package com.fls.animecommunity.animesanctuary.controller.rest;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fls.animecommunity.animesanctuary.dto.UserDto;
import com.fls.animecommunity.animesanctuary.service.interfaces.NotificationService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/users/notification")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/sse")
    public SseEmitter streamSse() {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 후 타임아웃
        
        emitter.onCompletion(() -> System.out.println("SSE completed"));
        emitter.onTimeout(() -> System.out.println("SSE timeout"));
        emitter.onError((ex) -> System.out.println("SSE error: " + ex.getMessage()));
        
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("message").data("Hello, this is a test message!"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 0, 5, TimeUnit.SECONDS); // 5초마다 메시지 전송

        return emitter;
    }
    
    @GetMapping("/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        // Authentication에서 Principal을 UserDto로 캐스팅
        UserDto userDto = (UserDto) authentication.getPrincipal();
        
        // 서비스를 통해 생성된 SseEmitter를 반환
        return notificationService.connectNotification(userDto.getId());
    }
}
