package com.tripwhiz.tripwhizuserback.member.repository;

import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@DataJpaTest
@Log4j2
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepoTests {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    @Commit
    public void insertDummies() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            MemberEntity member = MemberEntity.builder()
                    .email("user" + i + "@example.com")
                    .pw("password" + i)
                    .build();
            memberRepository.save(member);
        });
    }

}
