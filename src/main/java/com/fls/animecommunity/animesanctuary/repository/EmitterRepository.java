package com.fls.animecommunity.animesanctuary.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

    private static final Logger log = LoggerFactory.getLogger(EmitterRepository.class);

    // 유저ID를 키로 SseEmitter를 해시맵에 저장할 수 있도록 구현했다.
    private final Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(Long userId, SseEmitter sseEmitter) {
        String key = getKey(userId);
        emitterMap.put(key, sseEmitter);
        log.info("Saved SseEmitter for {}", userId);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(Long userId) {
        String key = getKey(userId);
        log.info("Got SseEmitter for {}", userId);
        return Optional.ofNullable(emitterMap.get(key));
    }

    public void delete(Long userId) {
        String key = getKey(userId);
        emitterMap.remove(key);
        log.info("Deleted SseEmitter for {}", userId);
    }

    private String getKey(Long userId) {
        return "Emitter:UID:" + userId;
    }
}
