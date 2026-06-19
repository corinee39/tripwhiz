package com.tripwhiz.tripwhizuserback.luggage.dto;

import lombok.Data;

@Data
public class LuggageDTO {

    private Point startPoint;
    private Point endPoint;
    private String email; // name을 통한 사용자 연결

    @Data
    public static class Point {
        private Double lat;
        private Double lng;
    }
}
