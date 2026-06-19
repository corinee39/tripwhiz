package com.tripwhiz.tripwhizuserback.luggage.dto;

import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageMoveStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LuggageMoveDTO {
    private Long lmno;
    private Spot sourceSpot;
    private Spot destinationSpot;
    private String email;
    private LocalDateTime moveDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LuggageMoveStatus status;
}
