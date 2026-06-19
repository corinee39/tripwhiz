package com.tripwhiz.tripwhizuserback.member.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private String email;
    private String pw;
    private String name;
    private String provider; //카카오 또는 구글을 구분하는 필드

}