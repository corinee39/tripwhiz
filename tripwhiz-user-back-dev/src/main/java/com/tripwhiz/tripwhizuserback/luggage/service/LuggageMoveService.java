package com.tripwhiz.tripwhizuserback.luggage.service;

import com.tripwhiz.tripwhizuserback.fcm.dto.FCMRequestDTO;
import com.tripwhiz.tripwhizuserback.fcm.service.FCMService;
import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageMoveDTO;
import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageMove;
import com.tripwhiz.tripwhizuserback.luggage.repository.LuggageMoveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class LuggageMoveService {

    private final LuggageMoveRepository luggageMoveRepository;
    private final RestTemplate restTemplate;
    private final FCMService fcmService;

    @Value("${server.store.owner.base.url}")
    private String storeOwnerBaseUrl;

    public LuggageMoveDTO createLuggageMove(LuggageMove luggageMove) {
        luggageMoveRepository.save(luggageMove);

        // 점주로 요청 전송
        String url = storeOwnerBaseUrl + "/api/storeowner/luggagemove/create";
        restTemplate.postForObject(url, luggageMove, Void.class);

        // 점주에게 FCM 알림 전송
        sendStoreOwnerNotification(luggageMove);

        return LuggageMoveDTO.builder()
                .lmno(luggageMove.getLmno())
                .sourceSpot(luggageMove.getSourceSpot())
                .destinationSpot(luggageMove.getDestinationSpot())
                .email(luggageMove.getEmail())
                .build();
    }

    private void sendStoreOwnerNotification(LuggageMove luggageMove) {
        String title = "새로운 수화물 이동 신청";
        String body = "출발: " + luggageMove.getSourceSpot().getSpotname() +
                ", 도착: " + luggageMove.getDestinationSpot().getSpotname();

        FCMRequestDTO request = FCMRequestDTO.builder()
                .token("STORE_OWNER_FCM_TOKEN") // 실제 점주 토큰으로 대체
                .title(title)
                .body(body)
                .build();

        fcmService.sendMessage(request);
    }
}
