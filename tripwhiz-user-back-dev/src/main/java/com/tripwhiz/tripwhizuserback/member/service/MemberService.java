package com.tripwhiz.tripwhizuserback.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.member.dto.MemberDTO;
import com.tripwhiz.tripwhizuserback.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${server.store.owner.base.url}")
    private String adminApiUrl;

    // 점주 서버로 멤버 전송
    public void sendMemberToAdminServer() {

        List<MemberEntity> members = memberRepository.findAll();

        log.info("members: " + members);

        if (members.isEmpty()) {
            log.info("No members found to send to Admin Server.");
            return;
        }

        // 각 멤버 데이터를 Admin 서버로 전송
        for (MemberEntity member : members) {
            // Admin 서버로 보낼 DTO 생성 (new 키워드 사용)
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setEmail(member.getEmail());
            memberDTO.setName(member.getName());
            memberDTO.setPw(member.getPw());

            String endpoint = "/api/admin/member/save";
            String adminApiEndpoint = adminApiUrl + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonRequest = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                jsonRequest = objectMapper.writeValueAsString(memberDTO);
                //jsonRequest = "{\"email\":\"sushiandcat@naver.com\"}";
            } catch (Exception e) {
                log.error("Failed to convert MemberDTO to JSON for member: {}", member.getEmail(), e);
                continue;
            }

            HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

            log.info("---------------------1");
            log.info("---------------------1");
            log.info(request);

            ResponseEntity<String> response = null;
            try {
                response = restTemplate.exchange(adminApiEndpoint, HttpMethod.POST, request, String.class);
            } catch (Exception e) {

                e.printStackTrace();
                log.error("Failed to send member to Admin API for member: {}", e);
                continue;
            }

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                log.info("Member successfully sent to Admin API: {}");
            } else {
                log.error("Failed to send member to Admin API for member: {}. Status code: {}", response != null ? response.getStatusCode() : "N/A");
            }
        }

    }

}
