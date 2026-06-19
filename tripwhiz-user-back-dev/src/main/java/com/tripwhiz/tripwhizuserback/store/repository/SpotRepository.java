package com.tripwhiz.tripwhizuserback.store.repository;


import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
