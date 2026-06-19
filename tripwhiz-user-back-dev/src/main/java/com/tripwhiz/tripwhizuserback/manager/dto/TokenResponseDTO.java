package com.tripwhiz.tripwhizuserback.manager.dto;

import lombok.Data;

@Data
public class TokenResponseDTO {

    private String accessToken;
    private String refreshToken;
}
