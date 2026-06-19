package com.tripwhiz.tripwhizuserback.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FCMRequestDTO {

    private String token; // FCM 토큰
    private String title; // 제목
    private String body; // 내용
    private Map<String, String> data; // 추가 데이터 (옵션)
}
