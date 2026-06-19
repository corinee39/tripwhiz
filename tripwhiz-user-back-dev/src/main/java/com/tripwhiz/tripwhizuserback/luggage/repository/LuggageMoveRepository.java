package com.tripwhiz.tripwhizuserback.luggage.repository;

import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageMove;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuggageMoveRepository extends JpaRepository<LuggageMove, Long> {
}
