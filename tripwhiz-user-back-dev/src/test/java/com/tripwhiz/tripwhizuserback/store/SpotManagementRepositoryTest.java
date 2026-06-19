package com.tripwhiz.tripwhizuserback.store;


import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import com.tripwhiz.tripwhizuserback.store.repository.SpotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
public class SpotManagementRepositoryTest {

    @Autowired
    private SpotRepository spotRepository;



    @Test
    public void insert100SpotsWithRandomOwners() {



        // 랜덤 객체 생성
        Random random = new Random();

        // Spot 데이터 100개 삽입
        for (int i = 1; i <= 100; i++) {
            Spot spot = new Spot();
            spot.setSpotname("Spot " + i); // 지점 이름
            spot.setAddress("Address " + i); // 지점 주소
//            spot.setTel("123-456-789" + i); // 전화번호
            spot.setDelFlag(false); // 삭제 여부



            spotRepository.save(spot);
        }

        System.out.println("100개의 Spot 데이터가 무작위 StoreOwner와 연결되어 저장되었습니다.");
    }
}
