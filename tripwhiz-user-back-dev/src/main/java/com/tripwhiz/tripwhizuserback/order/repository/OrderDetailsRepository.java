package com.tripwhiz.tripwhizuserback.order.repository;

import com.tripwhiz.tripwhizuserback.order.domain.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findByOrderOno(Long ono);
}

