package com.tripwhiz.tripwhizuserback.member.controller;

import com.tripwhiz.tripwhizuserback.member.dto.MemberDTO;
import com.tripwhiz.tripwhizuserback.member.dto.TokenResponseDTO;
import com.tripwhiz.tripwhizuserback.member.exception.MemberExceptions;
import com.tripwhiz.tripwhizuserback.member.service.GoogleService;
import com.tripwhiz.tripwhizuserback.security.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@Log4j2
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleService googleService;

    private final JWTUtil jwtUtil;

    @Value("${com.tripwhiz.accessTime}")
    private int accessTime;

    @Value("${com.tripwhiz.refreshTime}")
    private int refreshTime;

    @Value("${com.tripwhiz.alwaysNew}")
    private boolean alwaysNew;

    // Google 로그인 시 토큰 생성 엔드포인트
    @RequestMapping("google")
    public ResponseEntity<TokenResponseDTO> googleToken(@RequestParam String accessToken) {

        log.info("Google access token:" + accessToken);

        // 구글 인증 토큰을 사용하여 사용자 정보를 조회
        MemberDTO memberDTO = googleService.authGoogle(accessToken);

        log.info("Retrieved MemberDTO: " +memberDTO);

        // 이메일을 확인한 후 적절한 상태 코드를 반환
        if (memberDTO != null && memberDTO.getEmail() != null) {
            // 사용자 정보를 토큰에 저장하기 위한 Claim 맵 생성
            Map<String, Object> claimMap = Map.of(
                    "email", memberDTO.getEmail()
            );

            // Access token과 Refresh token을 생성
            String generatedAccessToken = jwtUtil.createToken(claimMap, accessTime);
            String refreshToken = jwtUtil.createToken(claimMap, refreshTime);

            // TokenResponseDTO를 구성
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
            tokenResponseDTO.setAccessToken(generatedAccessToken);
            tokenResponseDTO.setRefreshToken(refreshToken);
            tokenResponseDTO.setEmail(memberDTO.getEmail());
            tokenResponseDTO.setName(memberDTO.getName());

            return ResponseEntity.ok(tokenResponseDTO);

        } else {
            // 사용자 정보를 찾지 못했거나 이메일이 없는 경우 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
    }

    // Refresh token을 사용하여 새로운 Access token을 발급하는 엔드포인트
    @PostMapping(value = "google/refreshToken",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TokenResponseDTO> googlerefreshToken(
            @RequestHeader("Authorization") String accessToken,
            String refreshToken) {

        // accessToken 또는 refreshToken이 없으면 예외를 던짐
        if (accessToken == null || refreshToken == null) {
            throw MemberExceptions.TOKEN_NOT_ENOUGH.get();
        }

        // accessToken이 "Bearer "로 시작하지 않는 경우 예외를 던짐
        if (!accessToken.startsWith("Bearer ")) {
            throw MemberExceptions.ACCESSTOKEN_TOO_SHORT.get();
        }

        // Bearer 부분을 제거한 access token 문자열을 추출
        String accessTokenStr = accessToken.substring("Bearer ".length());

        // AccessToken의 만료 여부 체크
        try {
            Map<String, Object> payload = jwtUtil.validateToken(accessTokenStr);

            // 유효한 경우 이메일 정보를 가져와 응답에 포함
            String email = payload.get("email").toString();
            String name = payload.get("name").toString();

            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
            tokenResponseDTO.setAccessToken(accessTokenStr);
            tokenResponseDTO.setEmail(email);
            tokenResponseDTO.setRefreshToken(refreshToken);
            tokenResponseDTO.setName(name);

            return ResponseEntity.ok(tokenResponseDTO);

        } catch (ExpiredJwtException ex) {
            // AccessToken이 만료된 경우

            // RefreshToken의 만료 여부를 확인
            try {
                Map<String, Object> payload = jwtUtil.validateToken(refreshToken);
                String email = payload.get("email").toString();
                String name = payload.get("name").toString();
                String newAccessToken = null;
                String newRefreshToken = null;

                // alwaysNew 설정에 따라 새 토큰을 생성할지 결정
                if(alwaysNew) {
                    Map<String, Object> claimMap = Map.of("email", email);
                    newAccessToken = jwtUtil.createToken(claimMap,accessTime);
                    newRefreshToken = jwtUtil.createToken(claimMap,refreshTime);
                }
                // 새로 생성된 토큰 정보를 응답에 포함
                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setAccessToken(newAccessToken);
                tokenResponseDTO.setRefreshToken(newRefreshToken);
                tokenResponseDTO.setEmail(email);
                tokenResponseDTO.setName(name);

                return ResponseEntity.ok(tokenResponseDTO);

            } catch (ExpiredJwtException ex2) {
                // RefreshToken까지 만료된 경우, 재로그인이 필요함을 예외로 던짐
                throw MemberExceptions.REQUIRE_SIGH_IN.get();
            }
        }
    }
}
