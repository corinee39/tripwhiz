package com.tripwhiz.tripwhizuserback.luggage.service;

import com.tripwhiz.tripwhizuserback.fcm.dto.FCMRequestDTO;
import com.tripwhiz.tripwhizuserback.fcm.service.FCMService;
import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageStorageDTO;
import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageStorage;
import com.tripwhiz.tripwhizuserback.luggage.repository.LuggageStorageRepository;
import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import com.tripwhiz.tripwhizuserback.store.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class LuggageStorageService {

    private final LuggageStorageRepository luggageStorageRepository;
    private final RestTemplate restTemplate;
    private final FCMService fcmService;
    private final SpotRepository spotRepository;

    @Value("${server.store.owner.base.url}")
    private String storeOwnerBaseUrl;

    public LuggageStorageDTO createLuggageStorage(LuggageStorage luggageStorage) {
        // 로컬 데이터베이스에서 Spot 확인
        Spot spot = spotRepository.findById(luggageStorage.getStorageSpot().getSpno())
                .orElseThrow(() -> new IllegalArgumentException("Spot not found for ID: " + luggageStorage.getStorageSpot().getSpno()));

        // 수화물 저장
        luggageStorage.setStorageSpot(spot);
        luggageStorageRepository.save(luggageStorage);

        // 어드민 서버로 요청 전송
        sendLuggageToAdmin(luggageStorage);

        // 스토어오너에게 알림 발송
        sendStoreOwnerNotification(luggageStorage);

        return LuggageStorageDTO.builder()
                .storageSpot(luggageStorage.getStorageSpot())
                .email(luggageStorage.getEmail())
                .build();
    }

    private void sendLuggageToAdmin(LuggageStorage luggageStorage) {
        String url = storeOwnerBaseUrl + "/api/storeowner/luggagestorage/create";
        restTemplate.postForObject(url, luggageStorage, Void.class);
        log.info("LuggageStorage sent to Admin Server: {}", luggageStorage.getLsno());
    }

    private void sendStoreOwnerNotification(LuggageStorage luggageStorage) {
        String title = "새로운 수화물 보관 요청";
        String body = "보관 지점: " + luggageStorage.getStorageSpot().getSpotname();

        FCMRequestDTO request = FCMRequestDTO.builder()
                .token("STORE_OWNER_FCM_TOKEN") // 실제 스토어오너 토큰으로 대체
                .title(title)
                .body(body)
                .build();

        fcmService.sendMessage(request);
    }
}
