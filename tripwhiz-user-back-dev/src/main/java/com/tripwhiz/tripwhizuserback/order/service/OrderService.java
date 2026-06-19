package com.tripwhiz.tripwhizuserback.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripwhiz.tripwhizuserback.cart.dto.CartListDTO;
import com.tripwhiz.tripwhizuserback.cart.repository.CartRepository;
import com.tripwhiz.tripwhizuserback.common.dto.PageRequestDTO;
import com.tripwhiz.tripwhizuserback.common.dto.PageResponseDTO;
import com.tripwhiz.tripwhizuserback.fcm.dto.FCMRequestDTO;
import com.tripwhiz.tripwhizuserback.fcm.service.FCMService;
import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.member.repository.MemberRepository;
import com.tripwhiz.tripwhizuserback.order.domain.Order;
import com.tripwhiz.tripwhizuserback.order.domain.OrderDetails;
import com.tripwhiz.tripwhizuserback.order.domain.OrderStatus;
import com.tripwhiz.tripwhizuserback.order.dto.OrderListDTO;
import com.tripwhiz.tripwhizuserback.order.dto.OrderProductDTO;
import com.tripwhiz.tripwhizuserback.order.dto.OrderReadDTO;
import com.tripwhiz.tripwhizuserback.order.repository.OrderDetailsRepository;
import com.tripwhiz.tripwhizuserback.order.repository.OrderRepository;
import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import com.tripwhiz.tripwhizuserback.store.repository.SpotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final SpotRepository spotRepository;
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final FCMService fcmService;
    private final ObjectMapper objectMapper;

    // application.yml 파일에서 User API URL을 불러와 변수에 저장
    @Value("${server.store.owner.base.url}")
    private String adminApiUrl;

    // 주문 생성
    public Long createOrder(String email, Long spno, LocalDateTime pickUpDate) {

        // 1. Cart 데이터 가져오기
        List<CartListDTO> cartItems = cartRepository.findCartItemsByMemberEmail(email);

        // 2. Spot 객체 가져오기
        Spot spot = spotRepository.findById(spno)
                .orElseThrow(() -> new IllegalArgumentException("해당 지점이 존재하지 않습니다."));

        MemberEntity member = memberRepository.findByEmail(cartItems.get(0).getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        // 2. Order 생성 (주문 요약 정보)
        Order order = Order.builder()
                .member(member)  // 첫 번째 상품의 회원 정보
                .spot(spot)  // Spot ID를 이용하여 Spot 객체 설정
                .createtime(LocalDateTime.now())  // 현재 시간을 생성 시간으로 설정
                .pickupdate(pickUpDate)  // 픽업 시간 설정
                .status(OrderStatus.PREPARING)  // 주문 상태 "PREPARING"
                .totalAmount(cartItems.stream().mapToInt(cart -> cart.getQty()).sum())  // 총 수량 계산
                .totalPrice(cartItems.stream().mapToInt(cart -> cart.getPrice() * cart.getQty()).sum())  // 총 가격 계산
                .delFlag(false)
                .statusChangeTime(null)
                .userFcmToken(null)
                .build();  // 빌더로 객체 생성

        log.info(order);

        order = orderRepository.save(order);  // 주문 저장

        log.info(order);

        // 3. OrderDetail 생성 (주문 상세 정보)
        List<OrderProductDTO> orderProductDTOList = new ArrayList<>();

        for (CartListDTO cart : cartItems) {
            // OrderDetail 생성
            OrderDetails orderDetails = OrderDetails.builder()
                    .order(order)                    // 주문 정보 설정
                    .pno(cart.getPno())       // 상품 번호 설정
                    .pname(cart.getPname())    // 상품명 설정
                    .price(cart.getPrice())          // 상품 가격 설정
                    .amount(cart.getQty())           // 수량 설정
                    .build();                        // 객체 생성

            orderDetailsRepository.save(orderDetails);

            log.info(orderDetails);

            // OrderProductDTO로 상품 정보 저장
            OrderProductDTO orderProductDTO = OrderProductDTO.builder()
                    .pno(cart.getPno())              // 상품 번호 설정
                    .pname(cart.getPname())          // 상품 이름 설정
                    .amount(cart.getQty())           // 수량 설정
                    .price(cart.getPrice())          // 상품 가격 설정
                    .build();

            orderProductDTOList.add(orderProductDTO);

            log.info(orderProductDTO);

        }


        // 4. OrderReadDTO로 주문 정보 생성
        OrderReadDTO orderReadDTO = OrderReadDTO.builder()
                .ono(order.getOno())
                .email(order.getMember().getEmail())
                .products(orderProductDTOList)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().toString())
                .pickUpDate(order.getPickupdate())
                .spno(spno)
                .qrCodePath(null) // QR 코드 경로 추가
                .build();

        log.info(orderReadDTO);

        // 5. 점주 서버로 주문 데이터 전송

//        log.info("Sending request to URL: {}", adminApiUrl + "/api/user/order/receive");

        try {
            // JSON 직렬화 테스트
            String json = objectMapper.writeValueAsString(orderReadDTO);
            log.info("Serialized JSON before sending: {}", json);

            // REST 요청
            restTemplate.postForEntity(adminApiUrl + "/api/storeowner/order/receive", orderReadDTO, Void.class);

        } catch (JsonProcessingException e) {
            // JSON 직렬화 오류 처리
            log.error("Error serializing orderReadDTO: {}", e.getMessage(), e);
            throw new IllegalStateException("주문 데이터를 직렬화할 수 없습니다.", e);

        } catch (RestClientException e) {
            // REST 요청 오류 처리
            log.error("Error sending data to store owner API: {}", e.getMessage(), e);
            throw new IllegalStateException("점주 서버에 주문 데이터를 전송할 수 없습니다.", e);
        }

        // 6. Cart 데이터 삭제
        cartRepository.softDeleteAllByEmail(email);

        return order.getOno();

    }


    // 내 주문 리스트 조회
    public PageResponseDTO<OrderListDTO> getUserOrders(String email, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
        Page<Order> result = orderRepository.findByMemberEmail(email, pageable);

        List<OrderListDTO> dtoList = result.getContent().stream()
                .map(order -> OrderListDTO.builder()
                        .ono(order.getOno())
                        .email(order.getMember().getEmail())
                        .spno(order.getSpot().getSpno())
                        .totalAmount(order.getTotalAmount())
                        .totalPrice(order.getTotalPrice())
                        .createTime(order.getCreatetime())
                        .pickUpDate(order.getPickupdate())
                        .status(order.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<OrderListDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    // 특정 주문 상세 조회
    public OrderReadDTO getOrderDetails(Long ono, String memberEmail) {

        // 주문 조회
        Order order = orderRepository.findById(ono)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 이메일 검증
        if (!order.getMember().getEmail().equals(memberEmail)) {
            throw new RuntimeException("Unauthorized access to order details.");
        }

        // OrderDetails 조회
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderOno(ono);

        // OrderDetails -> OrderProductDTO 변환
        List<OrderProductDTO> products = orderDetailsList.stream()
                .map(details -> OrderProductDTO.builder()
                        .pno(details.getPno())         // 번호
                        .pname(details.getPname())     // 이름
                        .amount(details.getAmount())   // 수량
                        .price(details.getPrice())     // 가격
                        .build())
                .collect(Collectors.toList());

        // Order -> OrderReadDTO 변환
        return OrderReadDTO.builder()
                .ono(order.getOno())
                .email(order.getMember().getEmail())
                .products(products)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .pickUpDate(order.getPickupdate())
                .spno(order.getSpot().getSpno())
                .build();

    }

    // 주문 취소
    public void cancelOrder(Long ono, String memberEmail) {
        Order order = orderRepository.findById(ono)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getMember().getEmail().equals(memberEmail)) {
            throw new RuntimeException("Unauthorized access to order.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled.");
        }

        order.changeStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 유저에게 FCM 알림 전송
        fcmService.sendMessage(FCMRequestDTO.builder()
                .token(order.getUserFcmToken())
                .title("주문 취소 알림")
                .body("주문 번호 " + ono + "이(가) 취소되었습니다.")
                .build());

        // 점주에게 FCM 알림 전송
        fcmService.sendMessage(FCMRequestDTO.builder()
                .token("store_owner_fcm_token")
                .title("주문 취소 알림")
                .body("주문 번호 " + ono + "이(가) 취소되었습니다.")
                .build());
    }

    // 지점 변경
    public void changeSpot(Long ono, Long newSpotId, String memberEmail) {
        Order order = orderRepository.findById(ono)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getMember().getEmail().equals(memberEmail)) {
            throw new RuntimeException("Unauthorized access to order.");
        }

        Spot newSpot = spotRepository.findById(newSpotId)
                .orElseThrow(() -> new RuntimeException("Spot not found"));

        order.setSpot(newSpot);
        orderRepository.save(order);

        // 점주에게 FCM 알림 전송
        fcmService.sendMessage(FCMRequestDTO.builder()
                .token("store_owner_fcm_token")
                .title("지점 변경 알림")
                .body("주문 번호 " + ono + "의 지점이 변경되었습니다.")
                .build());
    }

    // 픽업 날짜 변경
    public void changePickUpDate(Long ono, LocalDateTime newPickUpDate, String memberEmail) {
        Order order = orderRepository.findById(ono)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getMember().getEmail().equals(memberEmail)) {
            throw new RuntimeException("Unauthorized access to order.");
        }

        order.setPickupdate(newPickUpDate);
        orderRepository.save(order);

        // 유저에게 FCM 알림 전송
        fcmService.sendMessage(FCMRequestDTO.builder()
                .token(order.getUserFcmToken())
                .title("픽업 날짜 변경 알림")
                .body("주문 번호 " + ono + "의 픽업 날짜가 변경되었습니다.")
                .build());

        // 점주에게 FCM 알림 전송
        fcmService.sendMessage(FCMRequestDTO.builder()
                .token("store_owner_fcm_token")
                .title("픽업 날짜 변경 알림")
                .body("주문 번호 " + ono + "의 픽업 날짜가 변경되었습니다.")
                .build());
    }

    // 특정 주문 항목의 QR 코드 이미지 가져오기
    public byte[] getOrderDetailsQRCodeImage(Long odno) {
        OrderDetails orderDetails = orderDetailsRepository.findById(odno)
                .orElseThrow(() -> new RuntimeException("Order details not found"));

        String qrCodeFileName = orderDetails.getQrCodePath(); // 파일명만 저장됨
        if (qrCodeFileName == null) {
            throw new RuntimeException("QR code not available for this order detail.");
        }

        String qrCodeUrl = "https://example.com/qrcode/" + qrCodeFileName; // 파일명으로 URL 구성
        return restTemplate.getForObject(qrCodeUrl, byte[].class);
    }

}
