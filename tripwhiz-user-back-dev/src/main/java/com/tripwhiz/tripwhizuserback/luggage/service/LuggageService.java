package com.tripwhiz.tripwhizuserback.luggage.service;

import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageDTO;
import com.tripwhiz.tripwhizuserback.luggage.entity.Luggage;
import com.tripwhiz.tripwhizuserback.luggage.repository.LuggageRepository;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LuggageService {

    private final LuggageRepository luggageRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public LuggageService(LuggageRepository luggageRepository, MemberRepository memberRepository, RestTemplate restTemplate) {
        this.luggageRepository = luggageRepository;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
    }

    // Luggage 저장
    public void saveLuggage(LuggageDTO luggageDTO) {
        // 이메일을 통해 사용자(MemberEntity) 확인
        MemberEntity member = memberRepository.findByEmail(luggageDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Member not found with email: " + luggageDTO.getEmail()));

        // Luggage 엔티티 생성 및 저장
        Luggage luggage = Luggage.builder()
                .startLat(luggageDTO.getStartPoint().getLat())
                .startLng(luggageDTO.getStartPoint().getLng())
                .endLat(luggageDTO.getEndPoint().getLat())
                .endLng(luggageDTO.getEndPoint().getLng())
                .member(member)
                .build();

        luggageRepository.save(luggage);
    }

    // 이메일을 기준으로 Luggage 목록 조회
    public List<Luggage> getLuggageByEmail(String email) {
        return luggageRepository.findByMemberEmail(email);
    }

    // QR 코드 가져오기 (RestTemplate 사용)
    public ResponseEntity<Resource> fetchQRCode(String type, String qrname) {
        String qrServiceUrl = "http://localhost:8082/api/storeowner/qrcode/view/" + type + "/" + qrname;
        return restTemplate.getForEntity(qrServiceUrl, Resource.class);
    }
}
