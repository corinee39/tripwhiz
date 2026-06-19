package com.tripwhiz.tripwhizuserback.luggage.repository;

import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuggageStorageRepository extends JpaRepository<LuggageStorage, Long> {
}
