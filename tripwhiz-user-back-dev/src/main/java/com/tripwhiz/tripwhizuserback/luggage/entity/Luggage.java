package com.tripwhiz.tripwhizuserback.luggage.entity;

import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Luggage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double startLat;
    private Double startLng;
    private Double endLat;
    private Double endLng;

    // MemberEntity와 email을 기준으로 연결
    @ManyToOne
    @JoinColumn(name = "member_email")  // email 기준으로 연결
    private MemberEntity member;
}
