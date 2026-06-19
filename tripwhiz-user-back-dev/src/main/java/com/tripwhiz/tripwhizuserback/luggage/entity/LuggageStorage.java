package com.tripwhiz.tripwhizuserback.luggage.entity;

import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LuggageStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lsno; // 보관 번호

    @ManyToOne
    @JoinColumn(name = "storagespno", nullable = false) // 보관 지점
    private Spot storageSpot;

    private String email;

    @CreatedDate
    @Builder.Default
    private LocalDateTime storageDate = LocalDateTime.now(); // 보관 신청 날짜

    @LastModifiedDate
    @Builder.Default
    private LocalDateTime storedUntil = LocalDateTime.now(); // 보관 종료 예정 날짜

    @Builder.Default
    private LuggageStorageStatus status = LuggageStorageStatus.PENDING;
}
