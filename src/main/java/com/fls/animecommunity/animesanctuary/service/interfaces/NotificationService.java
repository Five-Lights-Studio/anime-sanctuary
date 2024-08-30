package com.fls.animecommunity.animesanctuary.service.interfaces;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

	SseEmitter connectNotification(Long id);

}
