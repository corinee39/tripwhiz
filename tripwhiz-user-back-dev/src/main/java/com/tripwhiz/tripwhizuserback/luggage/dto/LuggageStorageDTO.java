package com.tripwhiz.tripwhizuserback.luggage.dto;

import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageStorageStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LuggageStorageDTO {
    private Long lsno;
    private Spot storageSpot;
    private String email;
    private LocalDateTime storageDate;
    private LocalDateTime storedUntil;
    private LuggageStorageStatus status;
}
