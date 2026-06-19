package com.tripwhiz.tripwhizuserback.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tripwhiz.tripwhizuserback.fcm.dto.FCMRequestDTO;
import com.tripwhiz.tripwhizuserback.fcm.exceptions.FCMMessageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessage(FCMRequestDTO fcmRequestDTO) {
        validateRequest(fcmRequestDTO); // 유효성 검사 분리

        // FCM 알림 생성
        Notification notification = Notification.builder()
                .setBody(fcmRequestDTO.getBody())
                .setTitle(fcmRequestDTO.getTitle())
                .build();

        // FCM 메시지 생성
        Message message = Message.builder()
                .setToken(fcmRequestDTO.getToken())
                .setNotification(notification)
                .build();

        try {
            String response = firebaseMessaging.send(message); // 메시지 전송
            log.info("FCM 메시지 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 메시지 전송 실패: {}", e.getMessage(), e);
            throw new FCMMessageException("FCM 메시지 전송 중 오류 발생: " + e.getMessage());
        }
    }

    // 유효성 검사 메서드
    private void validateRequest(FCMRequestDTO fcmRequestDTO) {
        if (fcmRequestDTO == null) {
            throw new FCMMessageException("fcmRequestDTO가 null입니다.");
        }
        if (fcmRequestDTO.getToken() == null || fcmRequestDTO.getToken().isEmpty()) {
            throw new FCMMessageException("fcmRequestDTO의 Token이 null이거나 비어 있습니다.");
        }
        if (fcmRequestDTO.getTitle() == null || fcmRequestDTO.getTitle().isEmpty()) {
            throw new FCMMessageException("fcmRequestDTO의 Title이 null이거나 비어 있습니다.");
        }
        if (fcmRequestDTO.getBody() == null || fcmRequestDTO.getBody().isEmpty()) {
            throw new FCMMessageException("fcmRequestDTO의 Body가 null이거나 비어 있습니다.");
        }
    }
}
