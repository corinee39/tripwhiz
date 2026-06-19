package com.tripwhiz.tripwhizuserback.member.service;

import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.member.dto.MemberDTO;
import com.tripwhiz.tripwhizuserback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class GoogleService {

    private final MemberRepository memberRepository;

    //구글 인증을 통해 사용자 정보를 가져오는 메서드
    public MemberDTO authGoogle(String accessToken) {

        log.info("---------authGoogle--------");

        // 구글 액세스 토큰을 사용하여 이메일을 가져옴
        String email = getGoogleAccountInfo(accessToken, "email");
        log.info("email: " + email);

        // 구글 액세스 토큰을 사용하여 이름을 가져옴
        String name = getGoogleAccountInfo(accessToken, "name");
        log.info("name: " + name);

        // 가져온 이메일로 사용자 정보를 조회
        Optional<MemberEntity> result = memberRepository.findById(email);
        MemberEntity memberEntity = null;
        MemberDTO memberDTO = new MemberDTO();

        // 기존 회원이면 해당 정보를 MemberDTO로 반환
        if (result.isPresent()) {
            memberEntity = result.get();
            memberDTO.setEmail(memberEntity.getEmail());
            memberDTO.setPw(memberEntity.getPw());
            memberDTO.setName(memberEntity.getName());
            memberDTO.setProvider(memberEntity.getProvider());

            return memberDTO;
        }
        // 회원이 존재하지 않으면, 새로운 회원을 생성
        String pw = UUID.randomUUID().toString(); // 새로운 사용자를 위한 임의의 비밀번호를 생성
        MemberEntity newMember = MemberEntity.builder()
                .email(email)
                .pw(pw)
                .name(name)
                .provider("google")
                .build();
        memberRepository.save(newMember); // 새로운 회원을 데이터베이스에 저장

        // 생성된 회원 정보를 MemberDTO로 반환
        memberDTO.setEmail(email);
        memberDTO.setPw(pw);
        memberDTO.setName(name);
        memberDTO.setProvider("google");

        return memberDTO;
    }

    // 구글 액세스 토큰을 사용하여 이메일을 가져오는 비공개 메서드
    private String getGoogleAccountInfo(String accessToken, String field) {

        String googleUserInfoURL = "https://www.googleapis.com/oauth2/v3/userinfo";

        // 토큰이 null이면 예외를 던짐
        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        // 외부 API 요청을 위한 RestTemplate 객체
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더를 설정, Authorization과 Content-Type을 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json");

        // 헤더를 포함한 HTTP 엔터티를 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Google API에 요청을 보냄
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    googleUserInfoURL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
            Map<String, Object> payload = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("Empty Google API response"));

            // 응답 로그 출력
            log.info("Google API Response: {}", payload);

            // 필요한 필드를 추출하고, 로그로 확인
            Object value = payload.get(field);
            String result = value instanceof String ? (String) value : null;
            log.info("Extracted {}: {}", field, result);

            // 이메일 또는 이름이 없다면 에러를 던짐
            if (result == null || result.isEmpty()) {
                throw new RuntimeException("No " + field + " found in Google API response");
            }
            return result;


        } catch (HttpClientErrorException e) {
            log.error("Error during Google API request: {}", e.getStatusCode());
            log.error("Response Body: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to retrieve email from Google API", e);
        }

    }
}
