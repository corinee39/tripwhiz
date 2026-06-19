package com.tripwhiz.tripwhizuserback.cart.repository;

import com.tripwhiz.tripwhizuserback.cart.domain.Cart;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest
@Log4j2
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartRepoTests {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void cartTest() {

        log.info(cartRepository.findAll());

    }

    @Test
    @Transactional
    @Commit
    public void insertDummies() {

        // 특정 Member 설정
        MemberEntity member = MemberEntity.builder()
                .email("eggzuxi99@gmail.com") // 이메일 설정
                .build();

        // 데이터베이스에서 모든 pno 값을 가져옴
        List<Long> productPnoList = productRepository.findAll().stream()
                .map(Product::getPno) // Product의 pno 추출
                .collect(Collectors.toList());

        if (productPnoList.isEmpty()) {
            throw new RuntimeException("No products found in the database.");
        }

        IntStream.rangeClosed(1, 10).forEach(i -> {
            // 무작위로 pno 선택
            Long randomPno = productPnoList.get((int) (Math.random() * productPnoList.size()));

            Cart cart = Cart.builder()
                    .member(member)
                    .product(Product.builder().pno(randomPno).build()) // 무작위로 선택한 product pno
                    .qty((int) (Math.random() * 5) + 1) // qty는 1에서 5 사이의 무작위 값
                    .delFlag(false)
                    .build();

            cartRepository.save(cart);
        });
    }

//    @Test
//    @Transactional
//    @Commit
//    public void insertDummies() {
//        MemberEntity member = MemberEntity.builder().mno(1L).build(); // member는 이미 존재하는 1번 회원을 사용
//        IntStream.rangeClosed(1, 10).forEach(i -> {
//            Product product = Product.builder().pno((long) (Math.random() * 10 + 1)).build(); // 무작위 product_pno 사용
//            Cart cart = Cart.builder()
//                    .member(member) // member를 1로 고정
//                    .product(product) // product 무작위 값
//                    .qty((int) (Math.random() * 5) + 1) // qty는 1에서 5 사이의 무작위 값
//                    .build();
//            cartRepository.save(cart);
//        });
//    }

}
