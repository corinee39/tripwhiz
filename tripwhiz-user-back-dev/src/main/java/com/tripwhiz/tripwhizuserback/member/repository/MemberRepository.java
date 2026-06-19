package com.tripwhiz.tripwhizuserback.member.repository;

import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    Optional<MemberEntity> findByEmail(String email);  //mj

}
