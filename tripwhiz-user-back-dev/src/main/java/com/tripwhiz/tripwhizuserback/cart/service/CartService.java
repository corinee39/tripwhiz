package com.tripwhiz.tripwhizuserback.cart.service;

import com.tripwhiz.tripwhizuserback.cart.domain.Cart;
import com.tripwhiz.tripwhizuserback.cart.dto.CartListDTO;
import com.tripwhiz.tripwhizuserback.cart.repository.CartRepository;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    // application.yml 파일에서 User API URL을 불러와 변수에 저장
    @Value("${server.store.owner.base.url}")
    private String adminApiUrl;

//    // 장바구니에 물건 추가
//    public void addToCart(CartListDTO cartListDTO) {
//        Product product = Product.builder().pno(cartListDTO.getPno()).build();

    // 장바구니에 물건 추가
    public void addToCart(CartListDTO cartListDTO) {
        Product product = Product.builder().pno(cartListDTO.getPno()).build();
        MemberEntity member = MemberEntity.builder().email(cartListDTO.getEmail()).build();

        Optional<Cart> existingCart = cartRepository.findByMemberEmailAndProductPno(cartListDTO.getEmail(), cartListDTO.getPno());

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();

//            if (cart.isDelFlag()) {
//                // del_flag가 true인 경우 다시 활성화 및 수량 설정
//                cart.setDelFlag(false);
//                cart.setQty(cart.getQty() + cartListDTO.getQty()); // 기존 수량에 새로운 수량 추가
//            } else {
//                // 기존 제품이 활성화 상태라면 수량 업데이트
//                log.info("Cart updated for product {}: Current Qty = {}, Added Qty = {}, delFlag = {}",
//                        cart.getProduct().getPno(), cart.getQty(), cartListDTO.getQty());
//                cart.setQty(cart.getQty() + cartListDTO.getQty());
//            }
            if (cart.isDelFlag()) {
                // del_flag가 true인 경우 다시 활성화 및 수량 설정
                cart.setDelFlag(false);
            }

            cart.setQty(cart.getQty() + cartListDTO.getQty()); // 기존 수량에 새로운 수량 추가
            log.info("Updated qty for product {}: Current Qty = {}, Added Qty = {}, delFlag = {}",
                    cart.getProduct().getPno(), cart.getQty(), cartListDTO.getQty(), cart.isDelFlag());
        } else {
            // 없으면 새로 추가
            Cart cart = Cart.builder()
                    .product(product)
                    .qty(cartListDTO.getQty())
                    .member(member)
                    .delFlag(false)
                    .build();
            cartRepository.save(cart);
        }
    }



    // 장바구니 목록 조회
    public List<CartListDTO> list(String email) {

        List<CartListDTO> cartItems = cartRepository.findCartItemsByMemberEmail(email);

        return cartItems.stream()
                .map(cart -> CartListDTO.builder()
                        .email(cart.getEmail())
                        .bno(cart.getBno())
                        .pno(cart.getPno())
                        .pname(cart.getPname())
                        .price(cart.getPrice())
                        .qty(cart.getQty())
                        .build())
                .collect(Collectors.toList());

    }

    // 상품 개별 삭제
    public void softDeleteByProduct(String email, Long pno) {
        Cart cart = cartRepository.findByMemberEmailAndProductPno(email, pno)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cart.softDelete(); // 엔티티의 softDelete 메서드 호출
    }

    // 상품 수량 변경
    public void changeQty(String email, Long pno, int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be less than zero.");
        }

        log.info("Changing quantity for email: {}, product ID: {}, quantity: {}", email, pno, qty);
        Cart cart = cartRepository.findByMemberEmailAndProductPno(email, pno)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found for product ID: " + pno));

//        cart.setQty(qty);
        if (qty == 0) {
            cart.setDelFlag(true); // 수량이 0이면 삭제 플래그 활성화
        } else {
            cart.setQty(qty); // 수량 설정
        }

        cartRepository.save(cart); // 변경 사항 저장
        log.info("Changed quantity for product ID: {} to {}", pno, qty);
    }

    // 장바구니 전체 비우기
    public void softDeleteAll(String email) {
        cartRepository.softDeleteAllByEmail(email);
    }


}
