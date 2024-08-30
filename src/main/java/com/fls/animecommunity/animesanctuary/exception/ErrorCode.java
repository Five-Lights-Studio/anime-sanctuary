package com.fls.animecommunity.animesanctuary.exception;

public enum ErrorCode {
    NOTIFICATION_CONNECTION_ERROR("Notification connection failed");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

