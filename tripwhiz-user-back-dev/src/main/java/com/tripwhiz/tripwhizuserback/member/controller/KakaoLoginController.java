package com.tripwhiz.tripwhizuserback.member.controller;

import com.tripwhiz.tripwhizuserback.member.dto.MemberDTO;
import com.tripwhiz.tripwhizuserback.member.dto.TokenResponseDTO;
import com.tripwhiz.tripwhizuserback.member.exception.MemberExceptions;
import com.tripwhiz.tripwhizuserback.member.service.KakaoService;
import com.tripwhiz.tripwhizuserback.security.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final JWTUtil jwtUtil;

    @Value("${com.tripwhiz.accessTime}")
    private int accessTime;

    @Value("${com.tripwhiz.refreshTime}")
    private int refreshTime;

    @Value("${com.tripwhiz.alwaysNew}")
    private boolean alwaysNew;

    @GetMapping("kakao")
    public ResponseEntity<TokenResponseDTO> kakaoToken(
            @RequestParam(required = false) String accessToken,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        accessToken = resolveAccessToken(accessToken, authorization);
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MemberDTO memberDTO = kakaoService.authKakao(accessToken);
        if (memberDTO == null || memberDTO.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(createTokenResponse(memberDTO.getEmail(), memberDTO.getName()));
    }

    @PostMapping(value = "kakao/refreshToken",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TokenResponseDTO> kakaoRefreshToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String refreshToken) {

        if (accessToken == null || refreshToken == null) {
            throw MemberExceptions.TOKEN_NOT_ENOUGH.get();
        }

        if (!accessToken.startsWith("Bearer ")) {
            throw MemberExceptions.ACCESSTOKEN_TOO_SHORT.get();
        }

        String accessTokenStr = accessToken.substring("Bearer ".length()).trim();

        try {
            Map<String, Object> payload = jwtUtil.validateToken(accessTokenStr);
            String email = payload.get("email").toString();
            String name = valueOrEmpty(payload.get("name"));

            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
            tokenResponseDTO.setAccessToken(accessTokenStr);
            tokenResponseDTO.setRefreshToken(refreshToken);
            tokenResponseDTO.setEmail(email);
            tokenResponseDTO.setName(name);

            return ResponseEntity.ok(tokenResponseDTO);
        } catch (ExpiredJwtException ex) {
            try {
                Map<String, Object> payload = jwtUtil.validateToken(refreshToken);
                String email = payload.get("email").toString();
                String name = valueOrEmpty(payload.get("name"));

                if (alwaysNew) {
                    return ResponseEntity.ok(createTokenResponse(email, name));
                }

                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setEmail(email);
                tokenResponseDTO.setName(name);
                tokenResponseDTO.setRefreshToken(refreshToken);
                return ResponseEntity.ok(tokenResponseDTO);
            } catch (ExpiredJwtException ex2) {
                throw MemberExceptions.REQUIRE_SIGH_IN.get();
            }
        }
    }

    private TokenResponseDTO createTokenResponse(String email, String name) {
        Map<String, Object> claimMap = Map.of(
                "email", email,
                "name", valueOrEmpty(name)
        );

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setAccessToken(jwtUtil.createToken(claimMap, accessTime));
        tokenResponseDTO.setRefreshToken(jwtUtil.createToken(claimMap, refreshTime));
        tokenResponseDTO.setEmail(email);
        tokenResponseDTO.setName(valueOrEmpty(name));
        return tokenResponseDTO;
    }

    private String resolveAccessToken(String accessToken, String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }

        return accessToken;
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}
