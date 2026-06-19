package com.tripwhiz.tripwhizuserback.fcm.exceptions;

public class FCMMessageException extends RuntimeException {

    public FCMMessageException(String message) {
        super(message);
    }

    public FCMMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
