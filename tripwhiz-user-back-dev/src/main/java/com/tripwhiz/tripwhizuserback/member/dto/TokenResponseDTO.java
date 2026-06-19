package com.tripwhiz.tripwhizuserback.member.dto;

import lombok.Data;

@Data
public class TokenResponseDTO {

    private String email;
    private String accessToken;
    private String refreshToken;
    private String name;

}