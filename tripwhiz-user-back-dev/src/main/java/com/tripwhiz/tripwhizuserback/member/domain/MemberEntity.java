package com.tripwhiz.tripwhizuserback.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class MemberEntity {

    @Id
    private String email;

    private String pw;

    private String name;

    private String provider;
}
